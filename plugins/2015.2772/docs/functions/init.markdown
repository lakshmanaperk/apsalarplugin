# GA.init()

| &nbsp;           | &nbsp;
| -----------------| ----------------------
| __Type__         | [function](http://docs.coronalabs.com/api/type/Function.html)
| __Library__      | [GA.*](Readme.markdown)

## Overview
Initalizes the Google Analytics SDK.



## Syntax
    GA.init(appKey)

##### appKey <small>(required)</small>
_[String](http://docs.coronalabs.com/api/type/String.html)._ The api key for
this app.



## Examples
``````lua
local GA = require 'plugin.googleanalytics'

GA.init("abcdef")
``````
