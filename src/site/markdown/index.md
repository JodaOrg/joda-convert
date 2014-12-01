## <i></i> About

**Joda-Convert** tackles the problem of round-trip Object to String conversion.

A common problem in serialization, particularly when working with textual formats
like JSON, is that of converting simple objects to and from strings.
Joda-Convert addresses this without getting caught up in the wider problem
of Object to Object transformation.

Joda-Convert is licensed under the business-friendly [Apache 2.0 licence](license.html).


## <i></i> Features

A selection of key features:

* Round-trip Object to String
* Conversions defined by annotations or by implementing a simple interface
* Very simple to use
* No dependencies


## <i></i> Documentation

Various documentation is available:

* The helpful [user guide](userguide.html)
* The [Javadoc](apidocs/index.html)
* The [change notes](changes-report.html) for each release
* The [GitHub](https://github.com/JodaOrg/joda-convert) source repository


---

## <i></i> Why Joda Convert?

Joda-Convert is a small, highly-focussed library, tackling a problem that the JDK should solve -
providing round-trip conversion between Objects and Strings.

```
  // conversion to String
  String str = StringConvert.INSTANCE.convertToString(foo);

  // conversion from String
  Foo bar = StringConvert.INSTANCE.convertFromString(Foo.class, str);
```

Joda-Convert supports two mechanisms of extending the list of supported conversions.
The first is to write your own converter implementing an interface.
The second is to use annotations.

The ability of Joda-Convert to use annotations to define the conversion methods is a key difference from other projects.
For example, most value classes, like `Currency` or `TimeZone`, already have methods
to convert to and from a standard format String.
Consider a `Distance` class:

```
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


---

## <i></i> Releases

[Release 1.7](download.html) is the current latest release.
This release is considered stable and worthy of the 1.x tag.
It depends on Java SE 6 or later.

The project runs on JDK 1.6 and has [no dependencies](dependencies.html).

Available in [Maven Central](http://search.maven.org/#artifactdetails%7Corg.joda%7Cjoda-convert%7C1.7%7Cjar).

```xml
<dependency>
  <groupId>org.joda</groupId>
  <artifactId>joda-convert</artifactId>
  <version>1.7</version>
</dependency>
```

---

### Support

Support on bugs, library usage or enhancement requests is available on a best efforts basis.

To suggest enhancements or contribute, please [fork the source code](https://github.com/JodaOrg/joda-convert)
on GitHub and send a Pull Request.

Alternatively, use GitHub [issues](https://github.com/JodaOrg/joda-convert/issues).
