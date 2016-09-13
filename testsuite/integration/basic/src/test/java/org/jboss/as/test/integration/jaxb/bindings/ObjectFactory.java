//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2011.08.30 at 03:04:17 PM CDT
//


package org.jboss.as.test.integration.jaxb.bindings;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.jboss.as.test.integration.jaxb.bindings package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _PurchaseOrder_QNAME = new QName("", "purchaseOrder");
    private static final QName _Comment_QNAME = new QName("", "comment");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.jboss.as.test.integration.jaxb.bindings
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Items }
     *
     */
    public Items createItems() {
        return new Items();
    }

    /**
     * Create an instance of {@link USAddress }
     *
     */
    public USAddress createUSAddress() {
        return new USAddress();
    }

    /**
     * Create an instance of {@link PurchaseOrderType }
     *
     */
    public PurchaseOrderType createPurchaseOrderType() {
        return new PurchaseOrderType();
    }

    /**
     * Create an instance of {@link Items.Item }
     *
     */
    public Items.Item createItemsItem() {
        return new Items.Item();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PurchaseOrderType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "purchaseOrder")
    public JAXBElement<PurchaseOrderType> createPurchaseOrder(PurchaseOrderType value) {
        return new JAXBElement<PurchaseOrderType>(_PurchaseOrder_QNAME, PurchaseOrderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "comment")
    public JAXBElement<String> createComment(String value) {
        return new JAXBElement<String>(_Comment_QNAME, String.class, null, value);
    }

}
