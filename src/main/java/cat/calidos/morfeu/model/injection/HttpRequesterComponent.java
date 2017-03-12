package cat.calidos.morfeu.model.injection;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.impl.client.CloseableHttpClient;

import com.google.common.util.concurrent.ListenableFuture;

import dagger.BindsInstance;
import dagger.producers.ProductionComponent;


@ProductionComponent(modules = {HttpRequesterModule.class, ListeningExecutorServiceModule.class})
public interface HttpRequesterComponent {

ListenableFuture<InputStream> fetchHttpData();
//ListenableFuture<String> fetchHttpDataAsString();
	
@ProductionComponent.Builder
interface Builder {
	@BindsInstance Builder forURI(URI u);
	@BindsInstance Builder withClient(CloseableHttpClient c);
	HttpRequesterComponent build();
}

}