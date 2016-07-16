Tap2phone
Originally released: Nov. 2, 2008

by Geoff Peters

----------------
Background info:

Back in 2006 I created a web site called Songtapper with some friends from school. It allows you to tap a song's rhythm on your space bar, and then the web site will analyze the rhythm and try and tell you what the song name is. The system worked amazingly well and was mentioned in a New York Times blog post, on BBC Radio, Global TV, Boston Channel, CBC Radio, etc. 

Later that year I decided to use the same algorithms and create a version for cell phones. However, my idea for cell phones is different. 

-------------------
What this app does:

The idea behind this app was that you would associate a different song with each of your friends and instead of dialing their number, you could just tap, like Jingle Bells to call Sam. It works pretty well but I never released it yet (until now!).

Ok, well I decided to go ahead and make the code GPL.... so you can have a look and decide for yourself how complicated it is! :)

The app is written in Java and should run on pretty much any cell phone that supports Java J2ME. It will also work in a J2ME emulator such as MicroEmulator (http://www.microemu.org/) if you don't have a Java cell phone. 

If you don't have the Java development environment setup, you can just install the JAR file on your phone to play around with the app first.

-----------------
How to use it:

This release, 0.0.1 is fully functional in terms of the rhythm detection algorithm, but it is not finished in terms of the user workflows. It also says something about being a 30 day evaluation version which I will remove when I get the chance (it won't ever expire). I just wanted to get the code out there on the web and I'll spend an afternoon tweaking with it sometime later.

When you start the program, it will ask you to begin tapping. Using one button, tap the rhythm of a simple song like Jingle Bells. It may help to sing aloud while tapping. Then select "Train" and enter the phone number of the person you'd like to associate with this song. In the future, when you start the app and tap that song, you will be prompted if you'd like to call that person you trained the app with.


-------------
How it works:

I've attached two research papers I co-wrote that describe how the algorithms and system work (there are actually three algorithms but only two are covered in the papers - the other one you'll have to get from reading the code). I've also attached a very useful paper on fast Approximate string matching using bit shifting, and I am using their exact algorithm that I ported to Java in my StringAlg.java class.

Here's a description of the other files:

TapToCall.java  and TapCanvas.java- general J2me app and canvas, handles user input of the tapping and records the timing of the taps. provides general application control.

TapAlgorithm.java - contains the contour string generation algorithms based on the tapping data. This is the stuff that is like gold. I'd much rather make these powerful algorithms open source and let people like yourself improve them and integrate them into new applications.

TapDatabase.java - a simple record store that holds people's phone numbers and the associated song that you should tap to call that person.
()


----------------------------

My hope is that by putting this code out there, other people will take advantage of the tapping algorithms and create some games or apps that use the rhythm analysis feature, which previously were only available through sites like Songtapper. I'd also like to open the project up to other programmers who would like to admin or contribute. Please email me at geoff@gpeters.com if you'd like to help out.

cheers,
Geoff