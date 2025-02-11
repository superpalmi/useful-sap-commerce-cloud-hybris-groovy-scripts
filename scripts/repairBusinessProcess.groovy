import de.hybris.platform.processengine.model.BusinessProcessModel
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import org.slf4j.LoggerFactory


flexibleSearchService = spring.getBean("flexibleSearchService")
modelService = spring.getBean("modelService")
businessProcessService = spring.getBean("businessProcessService")
LOG = LoggerFactory.getLogger(this.class);

println("groovy class: " + this.class)
LOG.info("groovy class: " + this.class)


String step = "myStep";

String query = "select {p.pk} from {BusinessProcess as p } " +
        "where {p.code} in (?orderCodes) and {p.processDefinitionName} =  ?process";

FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query);

flexibleSearchQuery.addQueryParameter("process", "myProcess");

List<BusinessProcessModel> result = flexibleSearchService.search(flexibleSearchQuery).getResult();

println("result size: " + result.size());

for (BusinessProcessModel businessProcessModel : result) {
    println("start BusinessProcessModel: " + businessProcessModel.getCode() + " step " + step);
    LOG.info("start BusinessProcessModel: " + businessProcessModel.getCode() + " step " + step);
    businessProcessService.restartProcess(businessProcessModel, step);

    //wait for x seconds before next process
    Thread.sleep(1000);

}

