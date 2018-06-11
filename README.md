## WeBikeSD, 2018 ##
San Diego, CA. USA

Project contributor: [Andrew Lam](https://github.com/andrewclam1991)

### Summary

This is the dev fork to modernize and refactor for WeBikeSD app usage.
Implements MVP architecture. 

Core Features: 
alpha goal
- contribution guideline and style
- start bike trip manually using device GPS
- show user trip distance 
- show user trip time
- save trip data locally 
- save trip per user account in remote backend (public domain)

beta goal
- start/stop bike trip automatically with activity recognition api
- show user option for enabling activity recognition (background service)
- write RESTful API for public and city to access bike ride data

release goal
- create eula and privacy notice
- refresh UI/UX


### Dependencies

* [RxJava](https://github.com/ReactiveX/RxJava)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [SqlBrite](https://github.com/square/sqlbrite)

## Features

### Complexity - understandability

#### Use of architectural frameworks/libraries/tools:

Building an app with RxJava is not trivial as it uses new concepts.

#### Conceptual complexity

Developers need to be familiar with RxJava, which is not trivial.

### Testability

#### Unit testing

Very High. Given that the RxJava ``Observable``s are highly unit testable, unit tests are easy to 
implement.

#### UI testing

TODO

## Attributions
This project is **built by the community**, updated and modified for San Diego's App deployment. 
Based on work from the following contributor(s), organization(s) and on the CycleTracks codebase 
for SFCTA.

#### Cycle Philly, 2013 Code for Philly #### 
Philadelphia, PA. USA

Author(s) and Contributor(s): 
 * Corey Acri <acri.corey@gmail.com>
 * Lloyd Emelle <lemelle@codeforamerica.org>
 * Kathryn Killebrew <banderkat@gmail.com>

#### Cycle Atlanta ####
Copyright 2012 Georgia Institute of Technology
Atlanta, GA. USA

Author(s) and Contributor(s): 
* Christopher Le Dantec <ledantec@gatech.edu>
* Anhong Guo <guoanhong15@gmail.com>

#### CycleTracks ####
Copyright 2009, 2010 San Francisco County Transportation Authority

100 Van Ness Ave FL26
San Francisco, CA 94102 USA

Author(s) and Contributor(s): 
* Billy Charlton <modeling@sfcta.org>

http://www.sfcta.org/cycletracks

CycleTracks is released under the GNU General Public License, version 3.
See 'COPYING' for a detailed description of the GNU GPL.


