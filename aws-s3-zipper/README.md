# Using aws-s3-zipper

-   Defaults to being told access keys
-   Similar speed to the java application I was wrote
-   Makes seperate zips however for each part (I'm still using a 1000 files)
-   Also loses file structure in the zip, so all files just get dumped in the zip
-   Can't specify output bucket or path.

-   Because of these limitations I forked the repo, and made it use my okta profile credentials.
-   Also made it able to specify output bucket.

This package seems good, only think is it's written in JavaScript, but it shouldn't be so complicated that not everyone can work on it. Ultimately we would still have to write an application and run it somewhere to do the zipping, so it might be easier to write a Java command line app so that it's something everyone is more familiar with?
