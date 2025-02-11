import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData
import de.hybris.platform.core.enums.OrderStatus
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.cronjob.model.CronJobModel
import de.hybris.platform.servicelayer.cronjob.PerformResult
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang.time.DateUtils
import org.slf4j.LoggerFactory
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.azure.dtu.DatabaseUtilization

import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalUnit
import java.util.stream.Collectors;


/**
 * Note: this groovy is available only with the Azure DTU extension on SAP Commerce Cloud.
 * this groovy script is used to check the database utilization and IO utilization and optionally pause the running cron jobs if the utilization is more than 80%
 *
 */
modelService = spring.getBean("modelService")
databaseUtilizationService = spring.getBean("databaseUtilizationService")
cronjobService = spring.getBean("cronJobService")
LOG = LoggerFactory.getLogger(this.class);

println("groovy class: " + this.class)
LOG.info("groovy class: " + this.class)


LOG.info("Executing DTUCheck");
println("Executing DTUCheck")


Double getDbUtilization() {
    Date startDate = new Date();

    Date endDate = DateUtils.addMinutes(startDate, 10);



    Instant start = Instant.parse(startDate.toInstant().toString());
    Instant end = Instant.parse(endDate.toInstant().toString());

    Duration duration = Duration.between(start, end);

    List<DatabaseUtilization> dataBaseUtilizationList = databaseUtilizationService.getUtilization(duration);

    Double sum = 0;

    Double avg = 0;

    int size = dataBaseUtilizationList.size();

    for (DatabaseUtilization databaseUtilization : dataBaseUtilizationList) {

        sum += databaseUtilization.getCpuUtilization();

        LOG.info(databaseUtilization.toString());
        println(databaseUtilization.toString());
    }

    avg = sum / size;

    LOG.info("dataBaseUtilization avg " + avg);
    println("dataBaseUtilization avg " + avg);

    return avg;
}

void findAndPauseCronJobs() {
    List<CronJobModel> cronJobs = cronjobService.getRunningOrRestartedCronJobs();

    for (CronJobModel cronJob : cronJobs) {
        LOG.info(cronJob.getCode());
        println(cronJob.getCode());

        cronJob.setStatus(CronJobStatus.PAUSED);
        cronJob.setActive(false);
        cronJob.setResult(CronJobResult.SUCCESS);
        modelService.save(cronJob);
        LOG.info("cron job " + cronJob.getCode() + " paused ");
        println("cron job " + cronJob.getCode() + " paused ");

    }
}

Double getIoUtilization() {
    Date startDate = new Date();

    Date endDate = DateUtils.addMinutes(startDate, 10);



    Instant start = Instant.parse(startDate.toInstant().toString());
    Instant end = Instant.parse(endDate.toInstant().toString());

    Duration duration = Duration.between(start, end);

    List<DatabaseUtilization> dataBaseUtilizationList = databaseUtilizationService.getUtilization(duration);

    Double sum = 0;

    Double avg = 0;

    int size = dataBaseUtilizationList.size();

    for (DatabaseUtilization databaseUtilization : dataBaseUtilizationList) {

        sum += databaseUtilization.getIoUtilization();

        LOG.info(databaseUtilization.toString());
        println(databaseUtilization.toString());
    }

    avg = sum / size;

    LOG.info("dataBaseUtilization avg " + avg);
    println("dataBaseUtilization avg " + avg);

    return avg;
}


Double dbUtilization = getDbUtilization();

LOG.info("dbUtilization " + dbUtilization);

if (dbUtilization > 80) {
    LOG.info("DB Utilization is more than 80%");
    println("DB Utilization is more than 80%");

    //UNCOMMENT THIS IF YOU NEED TO STOP JOBS IF DB UTILIZATION IS MORE THAN 80%
    //findAndPauseCronJobs()


}

Double ioUtilization = getIoUtilization();
LOG.info("ioUtilization " + ioUtilization);

if (ioUtilization > 80) {

    LOG.info("DB Utilization is more than 80%");
    println("DB Utilization is more than 80%");
    //UNCOMMENT THIS IF YOU NEED TO STOP JOBS IF DB UTILIZATION IS MORE THAN 80%
    //findAndPauseCronJobs()

}
