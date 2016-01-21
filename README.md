# Muddler

Muddlerは、複数種類、複数コネクションのRDBから、スクリプトで動的に作成したSQLで問い合わせを行える、ブラウザベースのSQLクライアントです。Java8で動作し、スクリプトはGroovy言語で作成できます。

Muddler is web based SQL client.

Muddler can connect multiple detabase types.(MySQL,PostgreSQL,Oracle,etc)

Muddler can connect multiple detabase connection.

Muddler can edit SQL dynamically using Groovy.


## Install

1.Add maven depencies muddler-groovy and jdbc driver.


```xml:pom.xml
<dependencies>
	<dependency>
		<groupId>com.grachro</groupId>
		<artifactId>muddler-groovy</artifactId>
		<version>0.0.9</version>
	</dependency>
	<dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
		<version>5.1.37</version>
	</dependency>
</dependencies>
<repositories>
	<repository>
		<id>grachro</id>
		<url>http://grachro.github.io/mvn-repo</url>
	</repository>
</repositories>
```

2.Create main class and call Muddler start.

```java:Sample.java
package your_package;

import com.grachro.muddler.Muddler;

public class Sample {
    public static void main(String[] args) {
        Muddler.start(args);
    }
}
```

3.Run and View
```
http://localhost:4567
```

