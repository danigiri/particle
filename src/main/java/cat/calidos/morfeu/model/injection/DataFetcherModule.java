/*
 *    Copyright 2016 Daniel Giribet
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package cat.calidos.morfeu.model.injection;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import cat.calidos.morfeu.problems.FetchingException;
import dagger.Lazy;
import dagger.producers.Producer;
import dagger.producers.ProducerModule;
import dagger.producers.Produces;
import dagger.producers.ProductionComponent.Builder;

/**
* @author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////
@ProducerModule
public class DataFetcherModule {

protected final static Logger log = LoggerFactory.getLogger(DataFetcherModule.class);


@Produces
public HttpGet produceRequest(URI uri) {
	return new HttpGet(uri);
}


@Produces
public ListenableFuture<InputStream> fetchData(URI uri,  
							 @Named("httpData") Producer<InputStream> httpData, 
							 @Named("fileData") Producer<InputStream> fileData ) 
									 throws FetchingException {
	if (uri.getScheme().equals("file")) {
		return fileData.get();
	} else {
		return httpData.get();
	}
}


@Produces @Named("httpData")
public InputStream fetchHttpData(CloseableHttpClient client, HttpGet request) throws FetchingException {

	try {
		 
		log.trace("Fetching http data from {}", request.getURI());
		// we want to close right now so we fetch all the content and close the input stream
		InputStream content = client.execute(request)
					 .getEntity()
					 .getContent();
		
			InputStream fetchedData = IOUtils.toBufferedInputStream(content);
			
			return fetchedData;

	} catch (Exception e) {
		throw new FetchingException("Problem fetching http data", e);
	} finally {
		if (client!=null) {
				try {
					client.close();
				} catch (IOException e) {
					throw new FetchingException("Problem closing client when fetching http data", e);
				}
		}
	}
	
}


@Produces @Named("fileData")
public InputStream fetchFileData(URI uri) throws FetchingException {
	
	try {
		
		log.trace("Fetching local data from {}",uri);
	
		return FileUtils.openInputStream(FileUtils.toFile(uri.toURL()));

	} catch (Exception e) {
		throw new FetchingException("Problem fetching local data at '"+uri+"'", e);
	}

}


}
