import de.hybris.platform.spring.HybrisContextLoaderListener
import org.springframework.web.context.ContextLoader
import org.springframework.web.context.WebApplicationContext

STOREFRONTCONTEXT = "ROOT"

def f = ContextLoader.getDeclaredField("currentContextPerThread")
f.setAccessible(true)

Map<ClassLoader, WebApplicationContext> contexts = f.get(HybrisContextLoaderListener)

for( context in  contexts.keySet()) {
    out.println context.getContextName()
}

def appContext = contexts.find {STOREFRONTCONTEXT.equals(it.key.getContextName())}
//When the context is found, we are ready to get our beans
if (appContext ==null) println "Impossible to retrieve application context"
else {
    defaultSyncService = appContext.value.getBean("nullSafeCurrentCategoryCmsCacheKeyProvider")
}
