## SpringBoot深度学习

### SpringBoot创建流程

#### 项目创建

- 插件创建

> 使用Spring Assistant直接创建SpringBoot项目并可图形化操作选择需要的组件

- 手动创建maven项目

> 1、根据官方文档，引入SpringBootStarterParent依赖
>
> <parent>
>         <groupId>org.springframework.boot</groupId>
>         <artifactId>spring-boot-starter-parent</artifactId>
>         <version>2.4.5</version>
>     </parent>
>
> 引入该依赖，官方文档中指出的一点是增加了依赖管理功能，只要在该依赖管理下的组件，version标签是不需要开发者手动配置，由SpringBoot统一进行版本管理
>
> 该依赖本身不提供任何依赖
>
> - SpringBoot引入parent之后，就可以进行版本依赖管理了，它是怎么做到的呢？
>
> > 进入parent依赖，发现其中还有一个父依赖
> >
> > <parent>
> > 		<groupId>org.springframework.boot</groupId>
> > 		<artifactId>spring-boot-dependencies</artifactId>
> > 		<version>2.4.5</version>
> > </parent>
> >
> > 再点进去dependencies依赖，发现它在properties属性中定义了许多可能会使用到的组件的版本，所以当我们使用组件的时候，如果不指定版本就默认使用SpringBoot默认的版本
>
> 2、增加Web依赖
>
> <dependencies>
> 	    <dependency>
> 	        <groupId>org.springframework.boot</groupId>
> 	        <artifactId>spring-boot-starter-web</artifactId>
> 	    </dependency>
> </dependencies>
>
> 引入该依赖，相当于是告诉SpringBoot我们需要用SpringBoot来开发Web应用。所以引入该依赖后，SpringBoot相当于给我们引入了tomcat和SpringMVC，那么显然，SpringBoot就需要在启动的时候去进行相关配置

#### 项目运行

- main方法 + @EnableAutoConfiguration

> 官方文档中指出，启动器需要和@EnableAutoConfiguration配合使用，其原因无非就是通过starter引入的组件需要在容器启动的时候进行相应的配置操作

- main方法 + @SpringBootApplication

> @SpringBootApplication包含了@EnableAutoConfiguration注解并有增强

### SpringBootApplication注解分析

SpringBootApplication注解的核心注解：

```properties
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

@SpringBootConfiguration 表明标注了@SpringBootApplication注解的类是个配置类，其中有个proxyBeanMethods属性，后面针对@Configuration注解的时候会分析

@ComponentScan 表明组件的扫描的一个范围或者说路径。默认是在当前该类及以下的路径，通过测试可以发现如果声明一个component组件在启动类之上，这个组件不会被SpringBoot加载到容器中

@EnableAutoConfiguration这个就是容器启动时的核心注解

- 第一个路径

> @EnableAutoConfiguration->@AutoConfigurationPackage->@Import(AutoConfigurationPackages.Registrar.class)
>
> 导入了一个Registrar类，这个类有一个方法registerBeanDefinitions方法，该方法的实现如下：
>
> register(registry, new PackageImports(metadata).getPackageNames().toArray(new String[0]))
>
> 接收一个注解元信息，这段意味着获取到注解所在的类的包路径，在包以下的组件都注册到容器中，这就解释了为什么组件在启动类之上不能被加载到容器中

- 第二个路径

> @EnableAutoConfiguration -> @Import(AutoConfigurationImportSelector.class) -> selectImports() -> getAutoConfigurationEntry() -> getCandidateConfigurations(annotationMetadata, attributes) -> SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),getBeanClassLoader())
>
> 最后，核心的代码如下：
>
> Enumeration<URL> urls = classLoader != null ? classLoader.getResources("META-INF/spring.factories") : ClassLoader.getSystemResources("META-INF/spring.factories");
>
> 也就是说，SpringBoot提供的starter其实在META-INF/路径下都有一个spring.factories文件用于配置需要加载到容器中的类，然后通过@EnableAutoConfiguration注解去读取该配置，实现组件注册

这就是SpringBoot自动装配的部分源码分析，到此通过服务启动，组件就已经注册到容器中了，但是是否是所有组件都会注册呢？其实不是，通过查看WebMvcAutoConfiguration的源码发现，组件的加载也是有条件的，通过@Conditional注解进行条件装配

#### 自动装配原理总结

- springboot先加载所有的自动配置类（xxxautoConfiguration）

- 每个自动配置类按照条件进行生效，默认都会绑定配置文件的值。xxxproperties类，配置类又是绑定application.properties文件的值

- 生效的配置类会给容器装配很多组件，只要容器中有了对应的组件，就相当于有了对应的功能

- 如果用户需要定制化配置，有两种实现方式：

  1、替换容器中的组件，@Configuration + @Bean 

  2、通过修改application.properties

### 部分注解介绍

- @Import

> import导入某个存在的类，使其注册为spring容器的组件

- @Conditional

> 例如conditionalOnBean，当存在该bean的时候，做什么conditonalOnMissingBean,当不存在该bean的时候，做什么，等等。

- @ImportResource

> 作用是把某个原来使用Spring的beans配置的内容通过SpringBoot的方式注册到spring容器中

- @ConfigurationProperties

> 该注解把配置文件的内容映射到实体类上，需要注意一点的是，该注解不会让实体类注册到容器中，所以需要手动把实体类进行注册，例如使用@Component或者在@Configuration配置类中使用@EnableConfigurationProperties

### 对配置类@Configuration注解的理解

> 1、以前Spring注册bean的方式，类似于beans.xml的形式。那么springboot取消了beans.xml文件之后，对于组件的注册就可以使用@Configuration注解
>
> 2、这个注解打在类上之后，意味着这个组件会组册到Spring容器中，可以通过SpringAplication.run方法的返回值（Spring上下文容器）查看到对应的bean
> 这个其实也就是可以理解为beans组件，那么暴露bean就从这个类进行操作
>
> 3、proxyBeanMethods属性的说明：这个属性的作用是是否由Spring容器代理对象，默认是true，也就意味着，如果存在对象A和B，对象A依赖与对象B的情况下
> 对象A在注册到Spring容器的时候，依赖了对象B（调了类内部的返回B的方法），这个时候Spring容器就会去检查对象B是否存在，存在则直接获取对象B，如果不存在就new一个新对象，如果是false的情况，依赖的处理就会变成对象A调用返回对象B的方法，Spring容器不会检查容器是否有该对象，而是直接生成B对象。那么最终的现象其实就是，A对象持有的B对象跟容器内的B的对象是不相等的。
>
> 这个解决一个什么问题。就是组件的依赖导致容器启动变慢甚至有循环依赖的问题。所以看到很多第三方的starter的autoConfiguration类的该属性都是false

### WebMvcAutoConfiguration源码分析

问题：表单提交，只支持post和get，从源码层面进行分析

> ```java
> @Bean
> @ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
> @ConditionalOnProperty(prefix = "spring.mvc.hiddenmethod.filter", name = "enabled", matchIfMissing = false)
> public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
>    return new OrderedHiddenHttpMethodFilter();
> }
> ```
>
> 发现有一个httpMethod的过滤器组件，但是默认是不开启的。
>
> ```java
> protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
>     HttpServletRequest requestToUse = request;
>     if ("POST".equals(request.getMethod()) && request.getAttribute("javax.servlet.error.exception") == null) {
>         String paramValue = request.getParameter(this.methodParam);
>         if (StringUtils.hasLength(paramValue)) {
>             String method = paramValue.toUpperCase(Locale.ENGLISH);
>             if (ALLOWED_METHODS.contains(method)) {
>                 requestToUse = new HiddenHttpMethodFilter.HttpMethodRequestWrapper(request, method);
>             }
>         }
>     }
> 
>     filterChain.doFilter((ServletRequest)requestToUse, response);
> }
> ```
>
> hiddenHttpMethod类的该方法的逻辑，如果是post且没有出现异常的情况下，需要去获取请求中携带的某个参数
>
> ```java
> private String methodParam = "_method";
> ```
>
> 也就是说，如果我们需要让表单提交的请求支持其他请求类型，需要做的事情如下：
>
> 1、开启WebMvcAutoConfiguration的httpMethod过滤器组件
>
> 2、请求中需要携带_method的请求参数
>
> ​	2.1、甚至可以修改掉methodParam属性的值，原因在于这个过滤器组件是否注册的一个条件为@ConditionalOnMissingBean(HiddenHttpMethodFilter.class)，如果容器中没有且配置为开启状态才注册，所以只需要自己通过配置类把该过滤器进行修改再暴露给容器即可

### 拦截器源码及原理分析

#### 源码分析

> 1、查看请求入口DispatchServlet类是否含有doGet/doPost方法，发现没有，寻找父类FrameworkServlet类
>
> ```java
> protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
>     this.processRequest(request, response);
> }
> ```
>
> 2、processRequest方法会执行一些初始化的工作，核心的方法是调用了doService方法
>
> ```java
> long startTime = System.currentTimeMillis();
> Throwable failureCause = null;
> LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
> LocaleContext localeContext = this.buildLocaleContext(request);
> RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
> ServletRequestAttributes requestAttributes = this.buildRequestAttributes(request, response, previousAttributes);
> WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
> asyncManager.registerCallableInterceptor(FrameworkServlet.class.getName(), new FrameworkServlet.RequestBindingInterceptor());
> this.initContextHolders(request, localeContext, requestAttributes);
> 
> try {
>     this.doService(request, response);
> } catch (IOException | ServletException var16) {
> ```
>
> 3、 doService方法包括一些对request对象的一些日志记录以及一些属性值的设置和核心的调用doDispatch方法
>
> ```java
> request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.getWebApplicationContext());
> request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
> request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
> request.setAttribute(THEME_SOURCE_ATTRIBUTE, this.getThemeSource());
> if (this.flashMapManager != null) {
>     FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
>     if (inputFlashMap != null) {
>         request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
>     }
> 
>     request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
>     request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, this.flashMapManager);
> }
> 
> try {
>     this.doDispatch(request, response);
> } finally {
>     if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted() && attributesSnapshot != null) {
>         this.restoreAttributesAfterInclude(request, attributesSnapshot);
>     }
> 
> }
> ```
>
> 4、doDispatch方法中找到方法执行的代码
>
> ```java
> if (!mappedHandler.applyPreHandle(processedRequest, response)) {
>     return;
> }
> 
> mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
> if (asyncManager.isConcurrentHandlingStarted()) {
>     return;
> }
> ```
>
> applyPreHandle方法就是在方法执行前的一段逻辑，拦截器的工作应该就是在这里
>
> ```java
> boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
>     HandlerInterceptor[] interceptors = this.getInterceptors();
>     if (!ObjectUtils.isEmpty(interceptors)) {
>         for(int i = 0; i < interceptors.length; this.interceptorIndex = i++) {
>             HandlerInterceptor interceptor = interceptors[i];
>             if (!interceptor.preHandle(request, response, this.handler)) {
>                 this.triggerAfterCompletion(request, response, (Exception)null);
>                 return false;
>             }
>         }
>     }
> 
>     return true;
> }
> ```
>
> 可以看到applyPreHandle方法的逻辑如下：
>
> 1、顺序的执行每个拦截器的preHandle方法且记录当前执行到第几个拦截器
>
> 1.1、当出现某个拦截器不通过的情况，则触发每个拦截器的afterCompletion方法，这里是倒序的执行每个已通过的拦截器的该方法
>
> 1.2 当所有拦截器都通过的情况下，那么handle就会直接执行
>
> 5、那么拦截器的postHandle方法是在什么时候调用的呢？
>
> ```java
> mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
> if (asyncManager.isConcurrentHandlingStarted()) {
>     return;
> }
> 
> this.applyDefaultViewName(processedRequest, mv);
> mappedHandler.applyPostHandle(processedRequest, response, mv);
> ```
>
> 真正的方法执行完后，会调用applyPostHandle方法
>
> ```java
> void applyPostHandle(HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndView mv) throws Exception {
>     HandlerInterceptor[] interceptors = this.getInterceptors();
>     if (!ObjectUtils.isEmpty(interceptors)) {
>         for(int i = interceptors.length - 1; i >= 0; --i) {
>             HandlerInterceptor interceptor = interceptors[i];
>             interceptor.postHandle(request, response, this.handler, mv);
>         }
>     }
> 
> }
> ```
>
> 会把拦截器的postHandle方法进行倒序执行

#### 拦截器的实现

- 实现HandlerInterceptor接口
- 使用配置类进行组件注册
- 配置类实现WebMvcConfigurer，使用其addInterceptors方法

#### 拦截器原理分析

> 如果存在拦截器A，B，C
>
> 1、当请求进来时，A，B，C的preHandle方法会顺序执行（A、B、C）
>
> 2、当有其中一个拦截器的preHandle方法没有通过的情况下，从该拦截器开始倒序执行已经执行过preHandle方法的拦截的afterCompletion方法（C、B、A）
>
> 3、当所有拦截器都顺利通过的情况下，倒序执行所有拦截器的posthandle（C、B、A）



