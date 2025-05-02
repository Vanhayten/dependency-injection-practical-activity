package framework.xml;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "beans")
@XmlAccessorType(XmlAccessType.FIELD)
public class BeanDefinition {
    @XmlElement(name = "bean")
    private List<Bean> beans = new ArrayList<>();

    public List<Bean> getBeans() {
        return beans;
    }

    public void setBeans(List<Bean> beans) {
        this.beans = beans;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Bean {
        @XmlAttribute
        private String id;

        @XmlAttribute
        private String className;

        @XmlElement(name = "property")
        private List<Property> properties = new ArrayList<>();

        @XmlElement(name = "constructor-arg")
        private List<ConstructorArg> constructorArgs = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public List<Property> getProperties() {
            return properties;
        }

        public void setProperties(List<Property> properties) {
            this.properties = properties;
        }

        public List<ConstructorArg> getConstructorArgs() {
            return constructorArgs;
        }

        public void setConstructorArgs(List<ConstructorArg> constructorArgs) {
            this.constructorArgs = constructorArgs;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Property {
        @XmlAttribute
        private String name;

        @XmlAttribute
        private String value;

        @XmlAttribute
        private String ref;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConstructorArg {
        @XmlAttribute
        private String value;

        @XmlAttribute
        private String ref;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }
    }
}