var S3Zipper = require("aws-s3-zipper");

var config = {
    useCredentials: true,
    profile: "okta",
    region: "eu-west-1",
    bucket: "fdp-feed-data-stage",
};

var zipper = new S3Zipper(config);

// zipper.zipToFile(
//     {
//         s3FolderName: "test",
//         startKey: null, // could keep null
//         zipFileName: "./myLocalFile.zip",
//         recursive: true,
//     },
//     function (err, result) {
//         if (err) console.error(err);
//         else {
//             var lastFile = result.zippedFiles[result.zippedFiles.length - 1];
//             if (lastFile) console.log("last key ", lastFile.Key); // next time start from here
//         }
//     }
// );

zipper.zipToFileFragments(
    // Max file size is that of the ZIP not the files going in the zip
    // I couldn't  seem to get max file count to work, not sure why.
    {
        s3FolderName:
            "messages/2022/1_UEFA Champions League/53106_Old Trafford/2032650_Man. United_Atalanta/2021-10-20/1800/delayed.samples.people.joints",
        startKey: null,
        zipFileName: "./myLocalFile.zip",
        maxFileSize: 1024 * 1024 * 50,
    },
    function (err, results) {
        if (err) console.error(err);
        else {
            if (results.length > 0) {
                var result = results[results.length - 1];
                var lastFile =
                    result.zippedFiles[result.zippedFiles.length - 1];
                if (lastFile) console.log("last key ", lastFile.Key); // next time start from here
            }
        }
    }
);

// TODO need to figure out how to specify output bucket

// zipper.zipToS3FileFragments(
//     {
//         s3FolderName:
//             "fdp-feed-data-stage/messages/2022/1_UEFA Champions League/53106_Old Trafford/2032650_Man. United_Atalanta/2021-10-20/1800/delayed.samples.people.joints/",
//         startKey: null, // optional
//         s3ZipFileName: "myS3File.zip",
//         maxFileCount: 1000,
//         maxFileSize: 1024 * 1024,
//         tmpDir: "/tmp", // optional, defaults to node_modules/aws-s3-zipper
//     },
//     function (err, results) {
//         if (err) console.error(err);
//         else if (results.length > 0) {
//             var result = results[results.length - 1];
//             var lastFile = result.zippedFiles[result.zippedFiles.length - 1];
//             if (lastFile) console.log("last key ", lastFile.Key); // next time start from here
//         }
//     }
// );
