package com.climate.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;

public class NewsExtractor {
	
	public String expanded_url="";
	
	public NewsArticle content(String strurl) {
		String title;
		String content;
		try {			
			URL url = new URL(strurl);
			String pageSource = getPageSource(strurl);
			
			final HTMLDocument htmlDoc = new HTMLDocument(pageSource);
			final TextDocument doc =  new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
			
			title = doc.getTitle();
			title = title.replace("\n", "").replace("\r", "");
			
			content = ArticleExtractor.INSTANCE.getText(pageSource);//.getText(doc);
			content = content.replace("\n", "").replace("\r", "");
	
		} catch (Exception e) {
			content = "";
			title = "";
		}
		
		NewsArticle article = new NewsArticle(title, content, strurl, expanded_url);
		return article;
	}
	
	public String getPageSource(String strurl) {
		try {
			URL url = new URL(strurl);
			HttpParams params = new BasicHttpParams();
			int timeoutConnection = 60000;
			HttpConnectionParams.setSoTimeout(params, 60000);
			HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
			
			DefaultHttpClient httpClient = new DefaultHttpClient(params);
			LSIRRedirectHandler modifiedRedirectHandler = new LSIRRedirectHandler();
			httpClient.setRedirectHandler(modifiedRedirectHandler);
			
			HttpGet request = new HttpGet(strurl);
			HttpResponse response = httpClient.execute(request);
			
			this.expanded_url = modifiedRedirectHandler.lastRedirectedURI.toString();
			
			// Get the response
			BufferedReader rd = new BufferedReader
			  (new InputStreamReader(response.getEntity().getContent()));
			    
			String line = "";
			String pageSource = "";
			while ((line = rd.readLine()) != null) {
			  pageSource += line;
			} 
			
			return pageSource;
			
		} catch (Exception e) {
			// TODO Auto-generated catch bloc
			return "";
		}
	}
	
	/**
	 * Don't use Exception protocol = http host = null
	 * @param strurl
	 * @return
	 * @throws Exception
	 */
	public String getPageSource1(String strurl) throws Exception {
		URL obj = new URL(strurl);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setReadTimeout(5000);
		conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
		conn.addRequestProperty("User-Agent", "Mozilla");
		conn.addRequestProperty("Referer", "google.com");
	 
	 
		boolean redirect = false;
	 
		// normally, 3xx is redirect
		int status = conn.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			if (status == HttpURLConnection.HTTP_MOVED_TEMP
				|| status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER)
			redirect = true;
		}
	 
		
	 
		if (redirect) {
	 
			// get redirect url from "location" header field
			String newUrl = conn.getHeaderField("Location");
			//System.out.println("Redirect to URL : " + newUrl);
			// get the cookie if need, for login
			String cookies = conn.getHeaderField("Set-Cookie");
	 
			// open the new connnection again
			conn = (HttpURLConnection) new URL(newUrl).openConnection();
			conn.setRequestProperty("Cookie", cookies);
			conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn.addRequestProperty("User-Agent", "Mozilla");
			conn.addRequestProperty("Referer", "google.com");
			this.expanded_url = newUrl;
			
		}
	 
		BufferedReader in = new BufferedReader(
	                              new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer html = new StringBuffer();
	 
		while ((inputLine = in.readLine()) != null) {
			html.append(inputLine);
		}
		in.close();
		return html.toString();
	}
	
	public static void main(String[] args) {
		
		String url = "http://t.co/TrKhXr7YeW";
				//"http://dotearth.blogs.nytimes.com/2014/08/03/new-study-sees-atlantic-warming-behind-a-host-of-recent-climate-shifts/";
				//
		NewsExtractor ext = new NewsExtractor();
		ext.content(url);
	}
}


class LSIRRedirectHandler extends DefaultRedirectHandler{

    private static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";

    public LSIRRedirectHandler() {
        super();
    }
    
    public URI lastRedirectedURI=null;
    
    public boolean isRedirectRequested(
            final HttpResponse response,
            final HttpContext context) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
        case HttpStatus.SC_MOVED_TEMPORARILY:
        case HttpStatus.SC_MOVED_PERMANENTLY:
        case HttpStatus.SC_SEE_OTHER:
        case HttpStatus.SC_TEMPORARY_REDIRECT:
            return true;
        default:
            return false;
        } //end of switch
    }

    public URI getLocationURI(
            final HttpResponse response, 
            final HttpContext context) throws ProtocolException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        //get the location header to find out where to redirect to
        Header locationHeader = response.getFirstHeader("location");
        if (locationHeader == null) {
            // got a redirect response, but no location header
            throw new ProtocolException(
                    "Received redirect response " + response.getStatusLine()
                    + " but no location header");
        }
//HERE IS THE MODIFIED LINE OF CODE
        String location = locationHeader.getValue().replaceAll (" ", "%20");

        URI uri;
        try {
            uri = new URI(location);            
        } catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid redirect URI: " + location, ex);
        }

        HttpParams params = response.getParams();
        // rfc2616 demands the location value be a complete URI
        // Location       = "Location" ":" absoluteURI
        if (!uri.isAbsolute()) {
            if (params.isParameterTrue(ClientPNames.REJECT_RELATIVE_REDIRECT)) {
                throw new ProtocolException("Relative redirect location '" 
                        + uri + "' not allowed");
            }
            // Adjust location URI
            HttpHost target = (HttpHost) context.getAttribute(
                    ExecutionContext.HTTP_TARGET_HOST);
            if (target == null) {
                throw new IllegalStateException("Target host not available " +
                        "in the HTTP context");
            }

            HttpRequest request = (HttpRequest) context.getAttribute(
                    ExecutionContext.HTTP_REQUEST);

            try {
                URI requestURI = new URI(request.getRequestLine().getUri());
                URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, true);
                uri = URIUtils.resolve(absoluteRequestURI, uri); 
            } catch (URISyntaxException ex) {
                throw new ProtocolException(ex.getMessage(), ex);
            }
        }

        if (params.isParameterFalse(ClientPNames.ALLOW_CIRCULAR_REDIRECTS)) {

            RedirectLocations redirectLocations = (RedirectLocations) context.getAttribute(
                    REDIRECT_LOCATIONS);

            if (redirectLocations == null) {
                redirectLocations = new RedirectLocations();
                context.setAttribute(REDIRECT_LOCATIONS, redirectLocations);
            }

            URI redirectURI;
            if (uri.getFragment() != null) {
                try {
                    HttpHost target = new HttpHost(
                            uri.getHost(), 
                            uri.getPort(),
                            uri.getScheme());
                    redirectURI = URIUtils.rewriteURI(uri, target, true);
                } catch (URISyntaxException ex) {
                    throw new ProtocolException(ex.getMessage(), ex);
                }
            } else {
                redirectURI = uri;
            }

            if (redirectLocations.contains(redirectURI)) {
                throw new CircularRedirectException("Circular redirect to '" +
                        redirectURI + "'");
            } else {
                redirectLocations.add(redirectURI);
            }
        }
        
        this.lastRedirectedURI = uri;
        
        return uri;
    }
}


