# Using aws-s3-zipper

-   Modified package to use profile credentials instead of hardcoding them
-   Much faster than the multi-part upload I was using (TODO figure out why)
-   Makes seperate zips however for each part (I'm still using a 1000 files)
-   Also loses file structure, so all files just get dumped in the zip
-   Means there's some logic there. Also probably want to fork and modify the aws-s3-zipper as required (like I did for the credentials thing)
-   This is to add things like specifying the output bucket etc.
-   This little tool seems good, only think is it's written in JavaScript, but it shouldn't be so complicated that not everyone can work on it
