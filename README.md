# foresite-toolkit
Automatically exported from code.google.com/p/foresite-toolkit; updated to include newer java dependencies 

Maven Repo:

        <repository>
            <id>internal</id>
            <name>Archiva Managed Internal Repository</name>
            <url>https://dev.tdar.org/archiva/repository/internal/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>snapshots</id>
            <name>Archiva Managed Snapshot Repository</name>
            <url>https://dev.tdar.org/archiva/repository/snapshots/</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>


     <dependency>
          <groupId>org.digitalantiquity.foresite-toolkit</groupId>
          <artifactId>foresite</artifactId>
          <version>0.2.1</version>
    </dependency>

Libraries for constructing, parsing, manipulating and serializing OAI-ORE Resource Maps.

Foresite is a JISC funded project which aims to produce a demonstrator and test of the OAI-ORE standard by creating Resource Maps of journals and their contents held in JSTOR, and delivering them as ATOM documents via the SWORD interface to DSpace. DSpace will ingest these resource maps, and convert them into repository items which reference content which continues to reside in JSTOR. The Python library is being used to generate the resource maps from JSTOR and the Java library is being used to provide all the ingest, transformation and dissemination support required in DSpace.

Both libraries support parsing and serialising in: ATOM, RDF/XML, N3, N-Triples, Turtle and RDFa

Please feel free to download and play with the code, and let us have your feedback via the Google group:

foresite@googlegroups.com
Project website: http://foresite.cheshire3.org/

Documentation:

http://code.google.com/p/foresite-toolkit/wiki/PythonLibrary
http://code.google.com/p/foresite-toolkit/wiki/JavaLibrary
Subversion Checkouts:

svn co http://foresite-toolkit.googlecode.com/svn/foresite-python/trunk/
svn co http://foresite-toolkit.googlecode.com/svn/foresite-java/trunk/
