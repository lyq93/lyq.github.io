#  SpringCloudEureka源码分析

## @EnableDiscoveryClient

> 一个应用要注册到服务中心或者是从服务中心获取服务列表，主要是需要做两件事情：
>
> - 在启动类打上@EnableDiscoveryClient的注解
> - 在application.properties中配置服务中心地址
>
> 那么，就从@EnableDiscoveryClient入手，查看该注解的注视，发现是开启DIscoveryClient的实例，通过类查找，发现一个spring自己提供的DiscoverClient的接口，一个是netflix提供的实现，通过查找上下结构，整理如下类的结构关系图

![image-20200925072327882](/Users/liuyongqian/Library/Application Support/typora-user-images/image-20200925072327882.png)

> 右边的LookupService、EurekaClient、DIscoveryClient都是netflix提供的，针对服务发现抽象的一些方法，而SpringCloud提供的DIscoverClient接口的好处是屏蔽了底层的实现细节，也就是意味着是使用eureka或是consul实现服务治理对于用户来说都是无感知的。
>
> 所以使用@EnableDiscoveryClient要比使用@EnableEurekaClient要好

## DiscoveryClient类

### 获取服务列表实现

> ```java
> @Deprecated
> @Override
> public List<String> getServiceUrlsFromConfig(String instanceZone, boolean preferSameZone) {
>     return EndpointUtils.getServiceUrlsFromConfig(clientConfig, instanceZone, preferSameZone);
> }
> ```

> ```java
> String region = getRegion(clientConfig);
>         String[] availZones = clientConfig.getAvailabilityZones(clientConfig.getRegion());
>         if (availZones == null || availZones.length == 0) {
>             availZones = new String[1];
>             availZones[0] = DEFAULT_ZONE;
>         }
> ```

> 进到EndpointUtils.getServiceUrlsFromConfig方法后，首先是上面这块代码逻辑，获取region以及region对应的zones

> ```java
> public static String getRegion(EurekaClientConfig clientConfig) {
>     String region = clientConfig.getRegion();
>     if (region == null) {
>         region = DEFAULT_REGION;
>     }
>     region = region.trim().toLowerCase();
>     return region;
> }
> ```

> - 从配置文件中读取region配置，如果没有则采用默认region配置(default)，从这里可以知道，一个应用对应一个region
> - 可以通过eureka.client.region自定义region

> ```java
> @Override
> public String[] getAvailabilityZones(String region) {
>    String value = this.availabilityZones.get(region);
>    if (value == null) {
>       value = DEFAULT_ZONE;
>    }
>    return value.split(",");
> }
> ```

> - 获取region对应的zones，region和zones的关系是一对多，同样的，先根据配置里的region去获取zone，没有获取到就采用默认的zone，默认的zone是很熟悉的defaultZone这个值，也就是eureka.client.serviceUrl.defaultZone这个配置的由来。
> - 从return的返回值可以知道，zone是可以配置多个的，通过逗号分隔

> ```java
> int myZoneOffset = getZoneOffset(instanceZone, preferSameZone, availZones);
> 
> List<String> serviceUrls = clientConfig.getEurekaServerServiceUrls(availZones[myZoneOffset]);
> if (serviceUrls != null) {
>     orderedUrls.addAll(serviceUrls);
> }
> ```

> 通过传入的参数按照一定的算法取出一个zone，然后serviceUrls就取自这个zone下

> ```java
> @Override
> public List<String> getEurekaServerServiceUrls(String myZone) {
>    String serviceUrls = this.serviceUrl.get(myZone);
>    if (serviceUrls == null || serviceUrls.isEmpty()) {
>       serviceUrls = this.serviceUrl.get(DEFAULT_ZONE);
>    }
>    if (!StringUtils.isEmpty(serviceUrls)) {
>       final String[] serviceUrlsSplit = StringUtils
>             .commaDelimitedListToStringArray(serviceUrls);
>       List<String> eurekaServiceUrls = new ArrayList<>(serviceUrlsSplit.length);
>       for (String eurekaServiceUrl : serviceUrlsSplit) {
>          if (!endsWithSlash(eurekaServiceUrl)) {
>             eurekaServiceUrl += "/";
>          }
>          eurekaServiceUrls.add(eurekaServiceUrl.trim());
>       }
>       return eurekaServiceUrls;
>    }
> 
>    return new ArrayList<>();
> }
> ```

> 这个方法就是根据传入的zone取出对应zone下的服务列表

- EurekaClientConfigBean

> ```java
> /**
>  * Eureka client configuration bean.
>  *
>  * @author Dave Syer
>  * @author Gregor Zurowski
>  */
> @ConfigurationProperties(EurekaClientConfigBean.PREFIX)
> public class EurekaClientConfigBean implements EurekaClientConfig, Ordered {
> 
>    /**
>     * Default prefix for Eureka client config properties.
>     */
>    public static final String PREFIX = "eureka.client";
> ```
>
> 这个类就解释了spring是如何读取到application.properties中的配置的，包括可以解释为什么从application.properties中配置eureka.client.region可以生效等问题
>
> - @ConfigurationProperties 表明该类是跟eurekaClient配置挂钩的类
> - 配置的前缀是eureka.client，那么肯定存在类似于enabled、region、serviecUrl的成员属性
>
> > ```java
> > /**
> >  * Flag to indicate that the Eureka client is enabled.
> >  */
> > private boolean enabled = true;
> > 
> > /**
> > 	 * Gets the region (used in AWS datacenters) where this instance resides.
> > 	 */
> > 	private String region = "us-east-1";
> > 
> > 	/**
> > 	 * Map of availability zone to list of fully qualified URLs to communicate with eureka
> > 	 * server. Each value can be a single URL or a comma separated list of alternative
> > 	 * locations.
> > 	 *
> > 	 * Typically the eureka server URLs carry protocol,host,port,context and version
> > 	 * information if any. Example:
> > 	 * https://ec2-256-156-243-129.compute-1.amazonaws.com:7001/eureka/
> > 	 *
> > 	 * The changes are effective at runtime at the next service url refresh cycle as
> > 	 * specified by eurekaServiceUrlPollIntervalSeconds.
> > 	 */
> > 	private Map<String, String> serviceUrl = new HashMap<>();
> > 
> > 	{
> > 		this.serviceUrl.put(DEFAULT_ZONE, DEFAULT_URL);
> > 	}
> > ```
>
> 所以通过this.serviceUrl.get("defaultZone")的方式就可以获取到application.properties里面的配置

- 简单实现

> ```java
> @Configuration
> @ConditionalOnWebApplication
> @ConditionalOnProperty(prefix = "person",name = "enabled", havingValue = "true", matchIfMissing = true)
> public class PersonAutoConfiguration {
>     @ConfigurationProperties(value = "person")
>     @Bean
>     public Person person(){
>         return new Person();
>     }
> }
> ```
>
> 把该类追加在配置自动配置类的文件的末尾即可，person类就类似于EurekaClientConfigBean，通过前缀+person类的属性的方式从application.properties中获取相应的配置

### 注册实例

> ```java
> @Inject
> DiscoveryClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config, AbstractDiscoveryClientOptionalArgs args,
>                 Provider<BackupRegistry> backupRegistryProvider, EndpointRandomizer endpointRandomizer) {
>   ...
>   // default size of 2 - 1 each for heartbeat and cacheRefresh
>             scheduler = Executors.newScheduledThreadPool(2,
>                     new ThreadFactoryBuilder()
>                             .setNameFormat("DiscoveryClient-%d")
>                             .setDaemon(true)
>                             .build());
>   ...
>   // finally, init the schedule tasks (e.g. cluster resolvers, heartbeat, instanceInfo replicator, fetch
>         initScheduledTasks();
> }
> ```

> 首先通过构造器发现初始化了一个调度器，这个是作为后续各种任务发起的准备工作。其次，核心的逻辑就是在initScheduledTasks()这个方法里面

> ```java
> /**
>  * Initializes all scheduled tasks.
>  */
> private void initScheduledTasks() {
> 	if (clientConfig.shouldRegisterWithEureka()) {
>             int renewalIntervalInSecs = instanceInfo.getLeaseInfo().getRenewalIntervalInSecs();
>             int expBackOffBound = clientConfig.getHeartbeatExecutorExponentialBackOffBound();
>             logger.info("Starting heartbeat executor: " + "renew interval is: {}", renewalIntervalInSecs);
> 
>             // Heartbeat timer
>             heartbeatTask = new TimedSupervisorTask(
>                     "heartbeat",
>                     scheduler,
>                     heartbeatExecutor,
>                     renewalIntervalInSecs,
>                     TimeUnit.SECONDS,
>                     expBackOffBound,
>                     new HeartbeatThread()
>             );
>             scheduler.schedule(
>                     heartbeatTask,
>                     renewalIntervalInSecs, TimeUnit.SECONDS);
> 
>             // InstanceInfo replicator
>             instanceInfoReplicator = new InstanceInfoReplicator(
>                     this,
>                     instanceInfo,
>                     clientConfig.getInstanceInfoReplicationIntervalSeconds(),
>                     2); // burstSize
> 
>             statusChangeListener = new ApplicationInfoManager.StatusChangeListener() {
>                 @Override
>                 public String getId() {
>                     return "statusChangeListener";
>                 }
> 
>                 @Override
>                 public void notify(StatusChangeEvent statusChangeEvent) {
>                     if (InstanceStatus.DOWN == statusChangeEvent.getStatus() ||
>                             InstanceStatus.DOWN == statusChangeEvent.getPreviousStatus()) {
>                         // log at warn level if DOWN was involved
>                         logger.warn("Saw local status change event {}", statusChangeEvent);
>                     } else {
>                         logger.info("Saw local status change event {}", statusChangeEvent);
>                     }
>                     instanceInfoReplicator.onDemandUpdate();
>                 }
>             };
> 
>             if (clientConfig.shouldOnDemandUpdateStatusChange()) {
>                 applicationInfoManager.registerStatusChangeListener(statusChangeListener);
>             }
> 
>             instanceInfoReplicator.start(clientConfig.getInitialInstanceInfoReplicationIntervalSeconds());
>         } else {
>             logger.info("Not registering with Eureka server per configuration");
>         }
> }
> ```

> 核心步骤：
>
> 1、首先读取配置是否需要注册到eureka
>
> 2、开启一个心跳检测的task
>
> 3、创建一个实现了Runnable接口的注册实例类InstanceInfoReplicator
>
> 5、调用instanceInfoReplicator的start方法

> ```java
> public void start(int initialDelayMs) {
>     if (started.compareAndSet(false, true)) {
>         instanceInfo.setIsDirty();  // for initial register
>         Future next = scheduler.schedule(this, initialDelayMs, TimeUnit.SECONDS);
>         scheduledPeriodicRef.set(next);
>     }
> }
> ```

> 1、这里有个需要说明的知识点就是started这个变量，这是个AtomicBoolean类，正常的基础类型的操作并不是原子性的，在并发的情况下是有问题的。所以这里采用了AtomicBoolean，compareAndSet这个操作就是原子性的。扩展的知识可以查阅乐观锁及CAS实现。
>
> 2、start方法就使用了构造器初始化的调度器发起任务调度

> ```java
> public void run() {
>     try {
>         discoveryClient.refreshInstanceInfo();
> 
>         Long dirtyTimestamp = instanceInfo.isDirtyWithTime();
>         if (dirtyTimestamp != null) {
>             discoveryClient.register();
>             instanceInfo.unsetIsDirty(dirtyTimestamp);
>         }
>     } catch (Throwable t) {
>         logger.warn("There was a problem with the instance info replicator", t);
>     } finally {
>         Future next = scheduler.schedule(this, replicationIntervalSeconds, TimeUnit.SECONDS);
>         scheduledPeriodicRef.set(next);
>     }
> }
> ```

> 在run方法里面，就调用了DiscoveryClient的regist()方法，进行服务注册

> ```java
> /**
>  * Register with the eureka service by making the appropriate REST call.
>  */
> boolean register() throws Throwable {
>     logger.info(PREFIX + "{}: registering service...", appPathIdentifier);
>     EurekaHttpResponse<Void> httpResponse;
>     try {
>         httpResponse = eurekaTransport.registrationClient.register(instanceInfo);
>     } catch (Exception e) {
>         logger.warn(PREFIX + "{} - registration failed {}", appPathIdentifier, e.getMessage(), e);
>         throw e;
>     }
>     if (logger.isInfoEnabled()) {
>         logger.info(PREFIX + "{} - registration status: {}", appPathIdentifier, httpResponse.getStatusCode());
>     }
>     return httpResponse.getStatusCode() == Status.NO_CONTENT.getStatusCode();
> }
> ```

> 这里就是发起一个REST请求，在register方法里面传入了一个instanceInfo对象，这个对象就是元数据信息。

### 服务获取





