jforth-config
=============

Distributed Configuration Management Support for local and centralized configuration

RoadMap:
----------------
Version-0.1.0(developing):
impl both local and centralized configuration with follow basic functions:
(1)load local config first and check
(2)if not exists, load remote config

It's a basic config loading priority(local>remote),that means you can keep empty config in local,it will load remote centralized config at last.

----------------
Version-0.2.0:
add functions:
(1)watch local config file change, it changes cause local configBundle change in directly.
(2)watch remote config item change, it changes cause remote configBundle change in directly.
