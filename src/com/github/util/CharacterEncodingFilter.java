package com.github.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * 
 * @author Dmitry Negoda <Dmitry.Negoda@gmail.com>
 */
public class CharacterEncodingFilter extends OncePerRequestFilter {

	private final int CONTENT_MAX_LENGTH = 100000;
	
	Logger log = Logger.getLogger(CharacterEncodingFilter.class.getName());
	
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// WA "небага" jetty http://jira.codehaus.org/browse/JETTY-1246 и http://jira.codehaus.org/browse/JETTY-1098
		String req = null;
		try {
			req = request.getQueryString();
		} catch (Exception e) {
			try {
				request.getClass().getMethod("setQueryEncoding", String.class).invoke(request, "ISO-8859-1");
				req = request.getQueryString();
			} catch (Exception refEx) {
				log.warning("Exception on setting query encoding:" + refEx.getMessage());
			}
		}
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			try {
				@SuppressWarnings("unused")
				Map testParams = request.getParameterMap();
				testParams = null;
			} catch (Exception e) {
				if (req != null && !req.isEmpty()) {
					final Map<String, String[]> params = new HashMap<String, String[]>();
					String[] reqParams = req.split("&");
					for (String s : reqParams) {
						String key; List<String> value = new ArrayList<String>();
						if (s.contains("=")) {
							int pos = s.indexOf("=");
							key = decodeString(s.substring(0, pos));
							if (params.containsKey(key)) {
								value.addAll(Arrays.asList(params.get(key)));
							}
							value.add(decodeString(s.substring(pos + 1)));
						} else {
							key = decodeString(s);
							value.add("");
						}
						params.put(key, value.toArray(new String[0]));
					}
					request = new HttpServletRequestWrapper(request) {
						@Override	public String getParameter(String paramName) {
							if (params.containsKey(paramName))
								return params.get(paramName)[0];
							else
								return null;
						}
						@Override	public String[] getParameterValues(String paramName) {
							if (params.containsKey(paramName))
								return params.get(paramName);
							else
								return null;
						}
						@Override	public Enumeration<String> getParameterNames() {
							Set<String> keys = params.keySet();
							return new IteratorToEnumeration<String>(keys.iterator());
						}
						@Override	public Map<String, String[]> getParameterMap() {
							return params;
						}
					};				
				}
			}
		}
		
		if (request.getContentType() != null && request.getContentType().startsWith("application/x-www-form-urlencoded")) {
			byte[] array = IOUtils.toByteArray(request.getInputStream());
			
			if (array.length > CONTENT_MAX_LENGTH) {
				try {
					response.sendError(413);
				} catch (Exception e) {
					log.warning(e.toString());
				}	
			}
						
			request.setAttribute("request_bytes", array);
			
			//log.fine(new String(array));
			
			MultiMap map = new MultiMap(16);
			Map<String, String[]> params = request.getParameterMap();
			for (Map.Entry<String, String[]> entry: params.entrySet()) {
				map.addValues(entry.getKey(), entry.getValue());
			}

			String trycharset = request.getCharacterEncoding() == null ? "UTF-8" : request.getCharacterEncoding();
			try {
				decode(array, map, trycharset);
			} catch (Exception ex) {
				trycharset = "windows-1252";
				decode(array, map, trycharset);
			}
			String charset = (String) map.getValue("charset", 0);
			if (charset != null && !charset.equalsIgnoreCase(trycharset)) {
				map.clear();
				for (Map.Entry<String, String[]> entry: params.entrySet()) {
					map.addValues(entry.getKey(), entry.getValue());
				}
				decode(array, map, charset);
			}
			
			final MultiMap newMap = map;
			request = new HttpServletRequestWrapper(request) {
				@Override
				public Enumeration getParameterNames() {
					Set keys = newMap.keySet();
					return new IteratorToEnumeration(keys.iterator());
				}
				
				public String getParameter(String name) {
					return (String) newMap.getValue(name, 0);
				}
				public Map getParameterMap() {
					return newMap.toStringArrayMap();
				}

				public String[] getParameterValues(String name) {
					List<String> values = newMap.getValues(name);
					if (values == null) {
						// see servlet spec. v2.5
						return null;
					} else {
						return values.toArray(new String[values.size()]);
					}
				}
			};			
		}
		filterChain.doFilter(request, response);
	}

	public void decode(byte[] array, MultiMap map, String trycharset) {
		UrlEncoded.decodeTo(new String(array), map, trycharset, CONTENT_MAX_LENGTH);
	}
	
	private String decodeString(String s) {
		if (s.contains("%")) {
			try {
				String decoded = URLDecoder.decode(s, "UTF-8");
				if (s.equals(URLEncoder.encode(decoded, "UTF-8")) ||
						s.equals(URLEncoder.encode(decoded, "UTF-8").replace("+", "%20"))) {
					return decoded;
				} else {
					decoded = URLDecoder.decode(s, "ISO-8859-1");
					if (s.equals(URLEncoder.encode(decoded, "ISO-8859-1"))) 
						return new String(decoded.getBytes(Charset.forName("UTF-8")), "UTF-8");
					else 
						return s;
				}
			} catch (UnsupportedEncodingException encException) {
				log.warning("Unsupported encoding:" + encException.getMessage());
			}
		}
		return s;
	}
}

class IteratorToEnumeration<T> implements Enumeration<T> {

	private final Iterator<T> iterator;

	IteratorToEnumeration(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	@Override
	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	@Override
	public T nextElement() {
		return iterator.next();
	}
}
