package com.mpaiement.soap.config;

import com.mpaiement.soap.security.SimplePasswordValidator;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet>
            messageDispatcherServlet(ApplicationContext ctx) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(ctx);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "paiement")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema paiementSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("PaiementPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://mpaiement.com/soap");
        wsdl.setSchema(paiementSchema);
        return wsdl;
    }

    @Bean
    public XsdSchema paiementSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/paiement.xsd"));
    }

    /**
     * Wss4jSecurityInterceptor = le garde de sécurité SOAP.
     * Il intercepte CHAQUE requête SOAP avant qu'elle atteigne
     * PaiementEndpoint et vérifie les credentials.
     *
     * Si credentials OK → laisse passer vers PaiementEndpoint
     * Si credentials KO → bloque et retourne une SOAP Fault
     */
    @Bean
    public Wss4jSecurityInterceptor securityInterceptor() {
        Wss4jSecurityInterceptor interceptor = new Wss4jSecurityInterceptor();

        // Dit à l'intercepteur quoi vérifier dans les requêtes entrantes
        // "UsernameToken" = cherche un bloc Username/Password dans le Header
        interceptor.setValidationActions("UsernameToken");

        // Notre validateur qui contient les credentials autorisés
        interceptor.setValidationCallbackHandler(new SimplePasswordValidator());

        return interceptor;
    }

    /**
     * addInterceptors = enregistre le garde de sécurité
     * sur TOUS les endpoints SOAP du microservice.
     * Chaque requête passera par lui avant d'arriver à PaiementEndpoint.
     */
    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(securityInterceptor());
    }
}