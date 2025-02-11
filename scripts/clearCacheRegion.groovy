/**
 *
 * Clear all cache regions in the cache, modify this script to clear specific cache regions, schedule it as a cronjob if needed
 */
import de.hybris.platform.regioncache.region.*

hacCacheFacade = spring.getBean("hacCacheFacade")

List<CacheRegion> cacheRegions = hacCacheFacade.getCacheRegions()

for(CacheRegion cacheRegion : cacheRegions) {
    println("Clearing cache region " + cacheRegion.getName() + "...")
    cacheRegion.clearCache()
    println("Cache region " + cacheRegion.getName() + " cleared!")
}

println("Cache cleared!")

