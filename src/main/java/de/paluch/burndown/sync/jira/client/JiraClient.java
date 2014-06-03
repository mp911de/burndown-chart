package de.paluch.burndown.sync.jira.client;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.BasicHttpContext;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpClient4xUtils;

/**
 * Jira Rest Client. <br>
 * <br>
 * Project: burdnown-chart <br>
 * Autor: mark <br>
 * Created: 25.03.2012 <br>
 * <br>
 */
public class JiraClient {

    private final String baseUrl;
    private final JiraRestClientProxy restProxy;
    private final BasicHttpContext localContext = new BasicHttpContext();
    private final DefaultHttpClient httpClient;
    private final  ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager();

    /**
     * @param baseUrl
     * @throws URISyntaxException
     */
    public JiraClient(String baseUrl) throws URISyntaxException {

        this.baseUrl = baseUrl;

        httpClient = new DefaultHttpClient(clientConnectionManager);
        ClientExecutor clientExecutor = new ApacheHttpClient4Executor(httpClient, localContext);

        ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();

        restProxy = ProxyFactory.create(JiraRestClientProxy.class, new URI(baseUrl), clientExecutor, providerFactory);

    }

    /**
     * Find Issues by Project-Key and version name.
     * 
     * @param projectKey
     * @param versionIdentifier
     * @return List of Issue-Keys.
     */
    public java.util.List<String> findSprintIssues(String projectKey, String versionIdentifier) {

        String jql = "project = " + projectKey + " and fixVersion = \"" + versionIdentifier + "\" order by issuekey";
        JiraRestSearchRequest request = new JiraRestSearchRequest();
        request.setJql(jql);
        JiraRestSearchResult result = restProxy.search(request);
        List<String> keys = new ArrayList<String>();
        for (JiraRestSearchResultIssue issue : result.getIssues()) {
            keys.add(issue.getKey());
        }

        return keys;

    }

    /**
     * Find Issues by Project-Key and version name.
     * 
     * @param projectKey
     * @param sprint
     * @return List of Issue-Keys.
     */
    public java.util.List<String> findRapidSprintIssues(String projectKey, String sprint) {

        String jql = "project = " + projectKey + " and sprint = \"" + sprint + "\" order by issuekey";
        JiraRestSearchRequest request = new JiraRestSearchRequest();
        request.setJql(jql);
        JiraRestSearchResult result = restProxy.search(request);
        List<String> keys = new ArrayList<String>();
        for (JiraRestSearchResultIssue issue : result.getIssues()) {
            keys.add(issue.getKey());
        }

        return keys;

    }

    /**
     * Retrieve Issue by Key.
     * 
     * @param key
     * @return JiraRestIssue
     */
    public JiraRestIssue getIssue(String key) {

        JiraRestIssue result = JiraCache.getInstance().getIssue(key, getLoader(key));
        return result;

    }

    private Callable<JiraRestIssue> getLoader(final String key) {
        return new Callable<JiraRestIssue>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public JiraRestIssue call() throws Exception {
                return restProxy.getIssue(key, "-comment");
            }

        };
    }

    /**
     * Login to Jira/Provide credentials.
     * 
     * @param username
     * @param password
     * @throws MalformedURLException
     */
    public void login(String username, String password) throws MalformedURLException {

        String token = username + ":" + password;
        String b64 = Base64.encodeBase64String(token.getBytes());

        ClientResponse<String> clientResponse = restProxy.performAuthentication("Basic " + b64);
        if (clientResponse.getStatus() >= 400) {
            throw new IllegalStateException("Cannot authenticate.");
        }

        if (true) {
            return;
        }

        Credentials credentials = new UsernamePasswordCredentials(username, password);
        httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        AuthCache authCache = new BasicAuthCache();

        BasicScheme basicAuth = new BasicScheme();
        URL url = new URL(baseUrl);

        authCache.put(new HttpHost(url.getHost(), url.getPort(), url.getProtocol()), basicAuth);
        localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
    }

    public void close()
    {
        clientConnectionManager.shutdown();
    }
}
