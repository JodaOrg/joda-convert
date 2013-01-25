
Joda-Convert
------------

Joda-Convert provides a small set of classes to aid conversion between Objects and Strings.
It is not intended to tackle the wider problem of Object to Object transformation.

```java
// conversion to String
String str = StringConvert.INSTANCE.convertToString(foo);

// conversion from String
Foo bar = StringConvert.INSTANCE.convertFromString(Foo.class, str);
```

Joda-Convert supports two mechanisms of extending the list of supported conversions.
The first is to write your own converter implementing an interface.
The second is to use annotations.

The ability of Joda-Convert to use annotations to define the conversion methods is a key difference from other projects.
For example, most value classes, like <code>Currency</code> or <code>TimeZone</code>, already have methods
to convert to and from a standard format String.
Consider a <code>Distance</code> class:

```java
public class Distance {

  @FromString
  public static Distance parse(String str) { ... }

  @ToString
  public String getStandardOutput() { ... }

}
```

As shown, the two methods may have any name. They must simply fulfil the required method signatures for conversion.
The <code>FromString</code> annotation may also be applied to a constructor.

When Joda-Convert is asked to convert between an object and a String, if there is no registered converter
then the annotations are checked. If they are found, then the methods are called by reflection.

Joda-Convert is licensed under the business-friendly [Apache 2.0 licence](https://github.com/JodaOrg/joda-convert/blob/master/LICENSE.txt).


### Documentation
Various documentation is available:

* The helpful [user guide](http://joda-convert.sourceforge.net/userguide.html)
* The javadoc for the [current release](http://joda-convert.sourceforge.net/api-release/index.html)
* The change notes for the [releases](http://joda-convert.sourceforge.net/changes-report.html)


### Releases
[Release 1.3](http://sourceforge.net/projects/joda-convert/files/joda-convert/1.3/) is the current latest release.
This release is considered stable and worthy of the 1.x tag.
It depends on JDK 1.5 or later.

Available in the [Maven Central repository](http://search.maven.org/#artifactdetails|org.joda|joda-convert|1.3|jar)


### Support
Please use GitHub issues and Pull Requests for support.


### History
Issue tracking and active development is at GitHub.
Historically, the project was at [Sourceforge](https://sourceforge.net/projects/joda-convert/).
The [home page](http://joda-convert.sourceforge.net/) is still at Sourceforge.
