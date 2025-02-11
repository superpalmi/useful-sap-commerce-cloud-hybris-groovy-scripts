import de.hybris.platform.util.Config;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

FlexibleSearchQuery flexibleSearchQuery=null;

String func_length, func_currDate;
if(Config.getParameter("db.driver").contains("Oracle")){//oracle
    println "processing for Oracle"
    func_length="length";
    func_currDate="sysdate";
} else if(Config.getParameter("db.driver").contains("sqlserver")){//azure
    println "processing for Azure"
    func_length="datalength";
    func_currDate="getDate()";
}

//Total number of promotions
query = "select count(*) from {PromotionSourceRule}";
flexibleSearchQuery = new FlexibleSearchQuery(query);
List l = new ArrayList();
l.add(Integer.class);
flexibleSearchQuery.setResultClassList(l);
SearchResult result = spring.getBean("flexibleSearchService").search(flexibleSearchQuery);
println "Total number of promotions: " + result.getResult().get(0);

//Total active promotions
query="select count(*) from {PromotionSourceRule as a join rulestatus as b on {a.status}={b.pk}} where {b.code}='PUBLISHED'";
flexibleSearchQuery = new FlexibleSearchQuery(query);
l = new ArrayList();
l.add(Integer.class);
flexibleSearchQuery.setResultClassList(l);
result = spring.getBean("flexibleSearchService").search(flexibleSearchQuery);
println "Total published promotions: " + result.getResult().get(0);

//Total published promotion but expired (date conditions)
query="select count(*) from {PromotionSourceRule as a join rulestatus as b on {a.status}={b.pk}} " +
        " where {b.code}='PUBLISHED'" +
        " and (" +
        "        ( ({a.endDate} is not null)  and   "+func_currDate+">{endDate}   )" +
        ")";
flexibleSearchQuery = new FlexibleSearchQuery(query);
l = new ArrayList();
l.add(Integer.class);
flexibleSearchQuery.setResultClassList(l);
result = spring.getBean("flexibleSearchService").search(flexibleSearchQuery);
println "Total published promotions but expired: " + result.getResult().get(0);

//Total active promotions per website
query="select {c.identifier},count({c.identifier}) as totalnum from {PromotionSourceRule as a join rulestatus as b on {a.status}={b.pk} join PromotionGroup as c on {a.website}={c.pk}} where {b.code}='PUBLISHED' group by {c.identifier} order by totalnum desc";
flexibleSearchQuery = new FlexibleSearchQuery(query);
l = new ArrayList();
l.add(String.class);
l.add(Integer.class);
flexibleSearchQuery.setResultClassList(l);
result = spring.getBean("flexibleSearchService").search(flexibleSearchQuery);
println "Total published promotions per website: "
for(int i=0;i<result.getResult().size();i++){
    println result.getResult().get(i);
}

//Total number of active Drools rules
query = "select count(*) from {droolsrule} where {active}=1 and {currentversion}=1";
flexibleSearchQuery = new FlexibleSearchQuery(query);
l = new ArrayList();
l.add(Integer.class);
flexibleSearchQuery.setResultClassList(l);
result = spring.getBean("flexibleSearchService").search(flexibleSearchQuery);
println "Total number of active Drools rules: "
println result.getResult().get(0);

//Total number of redundant Drools rules
query = "select count(r.pk) from ({{ select {pk} as pk, {code} as code, {kieBase} as kieBase, {version} as version from {DroolsRule} }}) r join ({{ select max({version}) as version, {code} as code, {kieBase} as kieBase from {DroolsRule} group by {code}, {kieBase}}}) m on r.code = m.code and r.kieBase = m.kieBase and r.version <> m.version"
flexibleSearchQuery = new FlexibleSearchQuery(query);
l = new ArrayList();
l.add(Integer.class);
flexibleSearchQuery.setResultClassList(l);
result = spring.getBean("flexibleSearchService").search(flexibleSearchQuery);
println "Total number of redundant Drools rules: "
println result.getResult().get(0);

//Average content size of active Drools rules
query="select {b.name},count({b.name}) as totalnum, sum("+func_length+"({a.rulecontent}))/count({b.name}) as avg_size from {droolsrule as a join DroolsKIEBase as b on {a.kiebase}={b.pk}} where {a.active}=1 and {a.currentversion}=1 group by {b.name} order by totalnum  desc";
flexibleSearchQuery = new FlexibleSearchQuery(query);
l = new ArrayList();
l.add(String.class);
l.add(Integer.class);
l.add(Integer.class);
flexibleSearchQuery.setResultClassList(l);
result = spring.getBean("flexibleSearchService").search(flexibleSearchQuery);
println "Average content size of active Drools rules:";
for(int i=0;i<result.getResult().size();i++){
    println result.getResult().get(i);
}

//Top 10 Drools rules per content size
query="select {a.code},"+func_length+"({a.rulecontent}) as rule_size from {droolsrule as a join DroolsKIEBase as b on {a.kiebase}={b.pk}} where {a.active}=1 and {a.currentversion}=1 and {b.name}='promotions-base' order by rule_size desc";
flexibleSearchQuery = new FlexibleSearchQuery(query);
l = new ArrayList();
l.add(String.class);
l.add(Integer.class);
flexibleSearchQuery.setResultClassList(l);
result = spring.getBean("flexibleSearchService").search(flexibleSearchQuery);
println "Top 10 Drools rules per content size: "
for(int i=0;i<(result.getResult().size()>=10?10:result.getResult().size());i++){
    println result.getResult().get(i);
}