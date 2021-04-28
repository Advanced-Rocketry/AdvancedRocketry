---
name: Bug report
about: Create a report to help us fix problems and improve your experience
title: "[BUG]"
labels: Unconfirmed Bug
assignees: ''

---

[Please fill out the form below and delete the sections in square brackets after reading them]

## Version of Advanced Rocketry
>[insert here

>Why is this important?
>Knowing the version of Advanced Rocketry helps us narrow down an offending piece of code faster because we can easily determine what code is added and when.]

## Have you verified this is an issue in the latest unstable build
- [x] Y
- [x] N
- [x] N/A

>[Why is this important?
>If you're not testing with the latest build, you may be encountering a bug that's already been fixed.
>Reporting bugs that have already been fixed wastes everyone's time.  (please look in open issues too, somebody else could have already reported it!)]

## Version of LibVulpes
>[insert here

>Why is this important?
>Advanced rocketry depends on a library mod (LibVulpes), some errors can really be in that mod.  So knowing the verison helps us not only start looking for fixes there without you needing to anwer more questions, it also helps us know if you have a version mismatch.]

## Version of Minecraft
>[insert here

>Why is this important?
>Different versions of minecraft have different codebases for Advanced Rocketry.  Knowing the version of minecraft (and thus AR) helps us pinpoint issues quicker!]

## Does this occur without other mods installed
- [x] Y
- [x] N
- [x] N/A
>If Y, what is the MINIMUM set of mods required.

>[Why is this important?  
>Sometimes interactions between mods cause issues.  While it can be a problem with Advanced Rocketry, sometimes it's not.  When there are a lot of mods, it can take bugtesters HOURS to go through the list to determine the root cause.  When there are a lot of bug reports, that can add DAYS to the time it takes to fix a large set of issues.]

## Crash report or log or visualVM (if applicable)
http://pastebin.com is a good place to put them
crash reports that are put in the issue itself are
hard to read
>[insert here

> If it's an issue involving lag, please submit a visualVM sampler snapshot.  Mods like lag goggles often do not provide the kind of information needed to debug lag.
> For more information on how to create a snapshot, please see "How to Run CPU Sampling" [here](http://greyfocus.com/2016/05/visualvm-sampling/).  Then export the snapshot as an .nps.  You may have to zip it since github is weird, then please upload it here.

>Why is this important?
>Full crashlogs contain valuable information like where exactly in the code the error occurred, what other mods may be running and what version of forge you have.  This helps us replicate and diagnose the issue FAR faster.]

## Description of the problem
>[How can you reliably reproduce the problem

>Why is this important?
>If we cannot reproduce the problem, we probably cannot fix it at all.]
