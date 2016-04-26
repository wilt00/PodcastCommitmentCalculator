# PodcastCommitmentCalculator
There are a lot of podcasts out there, and many of them are too long. You may find yourself with a playlist that fills up faster than you can keep up with. PCC is here to help with that.

PCC is a Java command line app that takes a list of podcast XML feeds and calculates the average time per week it would take to listen to them all.

Usage:

    PodcastCommitmentCalculator [path to file with feeds] [optional: number of weeks to use when calculating average]

testfeed.txt is an example of how PCC expects feedlist file to be formatted (one feed URL per line).

If blank, the default number of weeks is 4.

To do:
* Calculate length for podcasts where duration is not provided by the feed
* Make arguments behave more UNIXy
* Package with a saner name - "PodcastCommitmentCalculator" is a bit of a mouthful to type every time you want to run the app.
