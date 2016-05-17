# ncWMS User Guide

This is the user guide for ncWMS - a piece of software for visualising and exploring enviromental data in a browser. It can be run on a server to make your data available over the web or locally for personal use.

To see the capabilities of ncWMS, we have a [demo site available](http://behemoth.nerc-essc.ac.uk/ncWMS2/Godiva3.html)

[ncWMS Licence](https://github.com/Reading-eScience-Centre/edal-java/releases/download/edal-1.0.4/licence.txt)

ncWMS is a [Web Map Service](https://en.wikipedia.org/wiki/Web_Map_Service) for geospatial data that are stored in CF-compliant NetCDF files. The intention is to create a WMS that requires minimal configuration: the source data files should already contain most of the necessary metadata. ncWMS is developed and maintained by the Reading e-Science Centre (ReSC) at the University of Reading, UK.

ncWMS v2 is build on top of the [EDAL](edal_user_guide.html) libraries developed by ReSC

This guide provides instructions on installing, setting up, and using ncWMS v2.

## Changes from ncWMS 1.x

The following is a list of some of the major changes from ncWMS 1.x:

*   Support for SLD styling
*   Easy configuration of additional styles based on SLD templates
*   Support for in-situ data visualisation from the Met Office EN3/4 databases
*   Many more palettes and a simple configuration for adding new ones
*   An improved Godiva3 web client
*   Moved security configuration to the servlet container
*   Moved build system to Maven - this makes it easier for 3rd parties to build the project and use the EDAL libraries in their own projects
*   Updated to the latest NetCDF libraries from Unidata
*   Some changes to the API:

*   GetMap only produces animations with the addition of an extra URL parameter: "animation"
*   GetFeatureInfo now only returns text/XML
*   GetTimeseries, GetVerticalProfile, are new methods which replace the previous PNG implementations for GetFeatureInfo

## Migrating from ncWMS 1.x

Configuration for ncWMS v2 is very similar to that for ncWMS v1.x. Whilst the dataset configuration has changed quite a bit, old `config.xml` files from 1.x versions can be used on v2 (but not the other way around). Therefore, to migrate from v1.x to v2, only two steps need to be taken:

*   Copy the `config.xml` file from its old location (`~/.ncWMS/`) to the v2 location (`~/.ncWMS2` by default - see below for configuration)
*   Configure your servlet container to add a security role for the ncWMS admin user (see below)

## Installation

### Standalone version

The standalone version of ncWMS requires no installation. It can be run from the command-line with the command `java -jar ncWMS-standalone.jar` . This will run the WMS server locally with no security for administration and configuration. It will be available at [http://localhost:8080/](http://localhost:8080/). All configuration etc. is identical to the standard version.

### Servlet Container

ncWMS is a Java servlet which runs on a servlet container such as Tomcat, JBoss, or Glassfish. Installation is servlet-container dependent, but there are no ncWMS-specific procedures for installation.

Once ncWMS is up-and-running, on first launch it will create a configuration file and logging directory. By default this is located in a directory named `.ncWMS2` in the home directory of the user running the servlet container. **Note that the user running the servlet container must have write access to their home directory. This is not always the case for system users such as _tomcat7_ or _nobody_.** To change the location of the server configuration, stop the servlet container, and edit the file `$WEBAPP_DIR/WEB-INF/classes/config.properties` . This configuration file may contain the property `configDir=$HOME/.ncWMS2` . Change/add this to reflect the desired directory and restart the servlet container.

### Security configuration

Security for the administration of ncWMS is delegated to the servlet container (in standalone mode there is no security on any administration). You should define a security role with the name `ncWMS-admin`, and add users with that role. For example, using Tomcat, you can add the following to `tomcat-users.xml`:

```
<role rolename="ncWMS-admin" />
<user username="admin" password="ncWMS-password" roles="ncWMS-admin"/>
```

Access to the administration interface would then be granted to a user with the name `admin` and the password `ncWMS-password`

## Configuration

Configuration of ncWMS is performed by either accessing the administration web interface ([http://serveraddress/ncWMS/admin/](http://serveraddress/ncWMS/admin/)) or by directly modifying `config.xml` in the configuration directory. It is recommended to use the web interface - precise documentation of the XML configuration file is beyond the scope of this guide.

Once any changes have been made on this page, click the "Save configuration" button to apply them.

### Adding datasets

On the admin page, new datasets can be added by filling in the information in the "Required Data" column of the Datasets section. All other fields are optional. Already-configured datasets can be modified here.

#### Required Data

*   ID: An alphanumeric identifier for the dataset. This should be unique on the server.
*   Title: The title of the dataset. This is displayed in menus etc. on the user interface.
*   Location: The location of the dataset. This should be either a location on disk or an OPeNDAP URL. _Note that locations should use slashes - not backslashes - regardless of operating system_. For example, on Windows a path such as `C:/Data/dataset.nc` should be used. If referring to a location on disk, glob expressions of the form `/mnt/data/dataset/**/**/*.nc` are valid. If such an expression refers to multiple NetCDF files, they will be interpreted as having non-overlapping time axes. Any other configuration of data spanning multiple files is not supported.

#### Optional Metadata

*   More Info URL: A URL containing more information about the dataset. This will appear when clicking the information button in the web interface.
*   Copyright: The copyright information for the dataset. This will appear when clicking the information button in the web interface as well as on graphs generated by ncWMS.

#### Status

Status information about the dataset will appear here

#### Refresh

*   Auto-refresh rate: Specifies how regularly the dataset will be scanned for changes
*   Force refresh: By ticking this box and saving the configuration, the dataset will be refreshed immediately

#### Options:

*   Disabled: Disables this dataset but keeps the configuration intact
*   Queryable: Whether or not to allow GetFeatureInfo requests to this dataset
*   Downloadable: Whether or not to allow data download for this dataset

#### Data reading class:

This is the Java class which should be used to read the data. This should be left blank to use the default gridded NetCDF data reader. ncWMS v2 also provides the reader `uk.ac.rdg.resc.edal.dataset.cdm.En3DatasetFactory` for reading data from the UK Met Office EN3/EN4 dataset ([http://www.metoffice.gov.uk/hadobs/en4/](http://www.metoffice.gov.uk/hadobs/en4/)). For custom data types you specify their class name here (see Development section for more information)

![](images/godiva-en3.png)

#### Remove?

Tick this box to completely remove this dataset from the configuration

### Configuring variables

Once a dataset has been added, a link will appear in the "Edit variables" column. By clicking this, all of the variables within the dataset can be individually configured. The properties which can be adjusted are:

*   Title: The title of the variable to appear in menus / the capabilities document
*   Default colour scale range: This is the value range which the data covers. When a dataset is added, this is populated with a range based upon a small sample of the data
*   Default palette: The name of default colour palette to use. Available palettes can be browsed in the Godiva interface by clicking on the colour bar. The names of the palettes can be found by hovering over them.
*   Default number of colour bands: The number of gradations of colour to use when plotting the variable. Must be between 2 and 250.
*   Default scaling: Whether to plot data on a linear or a logarithmic colour scale.

### Dynamic services

Dynamic services are equivalent to datasets but are not pre-indexed. This allows users to access potentially very large numbers of files without having to configure them. An explanation of dynamic services is provided on the administration interface and their configuration is very similar to that of standard datasets.

### Other server settings

#### Cache

To increase speed, ncWMS uses a cache of recently-extracted data. Enabling/disabling the cache can be done here, as well as configuration of how much memory the cache is allow to consume. The higher this is, the more features will be cached.

#### Server settings

*   Title: The server name, which will be the title of the Godiva interface, and will also appear in the capabilities document
*   Abstract: Description of the purpose of this server - appears in the capabilities document
*   Keywords: Keywords describing this server - appear in the capabilities document
*   URL: A URL of the service provider - appears in the capabilities document
*   Max image width: The maximum allowable width (in pixels) which can be requested in a GetMap call
*   Max image height: The maximum allowable height (in pixels) which can be requested in a GetMap call
*   Allow GetFeatureInfo: This can be used to disable GetFeatureInfo requests globally on the server. If this is enabled, individual datasets can still have GetFeatureInfo disabled using the "Queryable" option
*   Allow global capabilities: Allows clients to request a WMS Capabilities document including all datasets on the server

#### Contact information

This configures the contact information which will appear in the capabilities document

## Usage

### Standard WMS

ncWMS provides the three mandatory methods of the WMS specification and should work with any standard WMS client. However, it also accepts a number of additional parameters for finer control of the images produced.

In ncWMS, the WMS layer names are of the form `datasetId/variableId`.

#### GetMap

All of the standard parameters for GetMap are supported on ncWMS. Additional notes on some of these:

*   FORMAT: The supported formats are:
    *   image/png
    *   image/png;mode=32bit
    *   image/gif
    *   image/jpeg
    *   application/vnd.google-earth.kmz
*   TIME: For gridded data with a discrete time axis this takes a single value. For in-situ data which is spread over a range, it is more useful to provide a time range in the form `starttime/endtime`. This should ideally be used in conjunction with the TARGETTIME parameter (see below)
*   ELEVATION: For gridded data with a discrete vertical axis this takes a single value. For in-situ data which is spread over a range, it is more useful to provide an elevation range in the form `startelevation/endelevation`. This should ideally be used in conjunction with the TARGETELEVATION parameter (see below)

In additional to the standard GetMap parameters, ncWMS accepts the following optional additional parameters. If not specified, the server-configured defaults are used:

*   COLORSCALERANGE: Of the form `min,max` this is the scale range used for plotting the data.
*   NUMCOLORBANDS: The number of discrete colours to plot the data. Must be between 2 and 250
*   ABOVEMAXCOLOR: The colour to plot values which are above the maximum end of the scale range. Colours are of the form 0xRRGGBB or 0xAARRGGBB, and it also accepts "transparent" and "extend"
*   BELOWMINCOLOR: The colour to plot values which are below the minimum end of the scale range. Colours are of the form 0xRRGGBB or 0xAARRGGBB, and it also accepts "transparent" and "extend"
*   LOGSCALE: "true" or "false" - whether to plot data with a logarithmic scale
*   TARGETTIME: For in-situ data, all points which fall within the time range (specified in the TIME parameter) will be plotted. In the case that an in-situ point has multiple time readings within that range, the colour used to plot them will depend on the time value which is closest to this given value
*   TARGETELEVATION: For in-situ data, all points which fall within the elevation range (specified in the ELEVATION parameter) will be plotted. In the case that an in-situ point has multiple elevation readings within that range, the colour used to plot them will depend on the elevation value which is closest to this given value
*   OPACITY: The percentage opacity of the final output image
*   ANIMATION: "true" or "false" - whether to generate an animation. This also needs the TIME to be of the form`starttime/endtime`, and currently is only implemented for features with a discrete time axis.

#### GetFeatureInfo

The GetFeatureInfo request works as per the standard WMS, with the following notable points:

*   INFO_FORMAT: `text/xml` and `text/plain` are supported
*   For in-situ (i.e. non-gridded) datasets, all features within a 9-pixel box of the click position are returned, up to the maximum specified by FEATURE_COUNT.

#### GetCapabilities

The GetCapabilities request works as per the standard WMS, with the following additional optional parameter:

*   DATASET: If this is present, a capabilities document will be returned containing only layers which are present within the given dataset ID

### Custom ncWMS methods

In additional to the standard WMS methods, ncWMS provides a number of additional requests. These all take the standard WMS parameters of `SERVICE=WMS`, `VERSION`, and `REQUEST`. The following are the valid values for the `REQUEST` parameter:

#### GetTimeseries

This produces either a timeseries graph or, if downloading is enabled, a CSV file containing the data. The URL parameters are identical to those of a GetFeatureInfo request. The `TIME` parameter should specify a range of times in the form `starttime/endtime`, and the supported formats are:

*   image/png
*   image/jpg
*   image/jpeg
*   text/csv

#### GetVerticalProfile

This produces either a vertical profile graph or, if downloading is enabled, a CSV file containing the data. The URL parameters are identical to those of a GetFeatureInfo request. The `ELEVATION` parameter should specify a range of elevations in the form `startelevation/endelevation`, and the supported formats are:

*   image/png
*   image/jpg
*   image/jpeg
*   text/csv

#### GetTransect

This produces a graph of data values along an arbitrary path. Additionally if there is vertical information present in the dataset, it will produce a vertical section along the same path.

It takes the same URL parameters as a GetMap request with the addition of a parameter `LINESTRING` of the format `x1 y1,x2 y2,x3 y3...` which defines the control points of the graph.

![](images/trajectory.png)

#### GetMetadata

The GetMetadata request is used to request small pieces of metadata from ncWMS. Many of these are also present in the capabilities document, but GetMetadata provides a more convenient method of accessing such data. GetMetadata always returns data in the JSON format. All GetMetadata requests must provide the parameter `ITEM` which states the type of metadata requested. `ITEM` can take the following values:

*   menu: Returns a tree representation of the available WMS layers, with IDs. Takes the optional parameter `DATASET` to return the same tree for a single dataset
*   layerDetails: Returns a set of details needed to plot a given layer. This includes such data as units, layer bounding box, configured scale range, etc. Takes the parameters `LAYERNAME` and `TIME`. The `TIME` parameter is optional, and if it is specified then the nearest available time is returned as part of the layer’s details.
*   minmax: Calculates the range of values in the given area. Takes the same parameters as a GetMap request.
*   timesteps: Returns the available times for a given day. Takes the parameters `LAYERNAME` and `DAY` (yyyy-mm-dd)
*   animationTimesteps: Returns a list of time strings at different temporal resolutions for a given time range. This is used to present to the user different frequencies for the generation of an animation. Takes the parameters `LAYERNAME`, `START`, and `END`

#### GetLegendGraphic

The GetLegendGraphic request generates an image which can be used as a legend. There are two main options: Generating just a colourbar, and generating a full legend. In the first case (the URL parameter `COLORBARONLY` is set to "true"), the following URL parameters are used:

*   PALETTE: The name of the palette to use. If missing, set to "default"
*   NUMCOLORBANDS: The number of colour bands to use. If missing, set to 250
*   VERTICAL: Whether to very colours vertically. If missing, defaults to true
*   WIDTH: The width of the image to generate. If missing, defaults to 50
*   HEIGHT: The height of the image to generate. If missing, defaults to 200

For a full legend, the additional parameters `LAYERS` and either `STYLES`, `SLD`, or `SLD_BODY` must be supplied. This is because a single WMS layer may depend on an arbitrary number of sub-layers, depending on the style it is plotted in. In addition to these parameters, the optional parameters controlling the style may be supplied (these are the same as documented in the GetMap request):

*   COLORSCALERANGE
*   NUMCOLORBANDS
*   ABOVEMAXCOLOR
*   BELOWMINCOLOR
*   LOGSCALE

Note that for full legends, the supplied width and height are NOT the final height of the image, but rather the width and height of each individual coloured plot area (i.e. the 1d/2d colourbar)

### Godiva3

Normal access to the WMS is done using a web client. ncWMS comes with Godiva3 - a WMS client written to take advantage of all of the extended WMS methods in ncWMS. It is accessed at [http://serveraddress/ncWMS/Godiva.html](http://serveraddress/ncWMS/Godiva.html)

Most of the controls on the Godiva3 interface have tool tips to help you: hover the mouse over the control in question to find out what it does.

#### Basic usage

Use the left-hand menu to select a variable for viewing (click on the variable’s name in the tree view). The data should appear on the interactive map after a short delay (a progress bar may appear showing the progress of loading image tiles from the ncWMS server(s)).

#### Navigating the map

The interactive map can be dragged with the mouse to navigate around. Using the controls in the top left of the map you can zoom in and out. You can zoom quickly to a particular area by holding down Shift and dragging a rectangle on the map. You can automatically centre the current data overlay on the map by clicking the "Fit layer" button (below the map, the button with 4 divergent arrows).

#### Selecting the vertical level

If the variable you are viewing has a vertical dimension you will be able to select the vertical level using the drop-down "Elevation / Depth" box above the map.

#### Selecting the timestep

If the displayed variable has a time dimension you will be able to select the date and time using the drop-down "Time" box above the map

#### Finding the data value at a point

Once a variable has been displayed on the map, you can click on the map to discover the data value at that point. The data value, along with the latitude and longitude of the point you clicked, will appear in a small pop-up window at the point where you clicked, as well as links to generate timeseries / vertical profile plots.

#### Changing the style of the data display

##### Adjusting the colour contrast range

When you first load up a variable it will appear with a default colour scale range. This range may not be appropriate for the geographical region and timestep you are interested in. By clicking the "auto" button (the colour-wheel button to the right of the colour bar) the colour scale range will be automatically stretched to suit the data currently displayed in the map. You can also manually change the colour scale range by editing the values at the top and bottom of the colour scale bar.

##### Locking the scale range

Sometimes, when comparing two datasets, you might want to fix the colour scale range so that when you select a new variable, that variable is shaded with exactly the same colour scale. To do this, click the "lock" button link (the padlock button to the right of the colour bar). The colour scale range will then not be changed when a new variable is loaded and the scale range cannot be edited manually. Click the button again to unlock the colour scale.

##### Changing the colour palette

The colour palette can be changed by clicking on the colour scale bar. A pop-up window will appear with the available palettes. Clicking the "Flip" button will invert all of the palettes. Click on one to load the new palette. The window also contains a drop-down box to select the number of colour bands to use, from 10 (giving a contoured appearance) to 250 (smoothed).

##### Changing plotting style

The style used to plot the variable can be adjusted using the topmost of the 3 drop-down boxes to the right of the colour bar. All styles which are compatible with the displayed data will be available, although not necessarily appropriate (for example, arrows are compatible with any scalar data type, but only meaningful when that data represents a direction in degrees).

##### Changing layer opacity

The overall opacity of the WMS layer can be adjusted using the drop-down box to the right of the colour bar.

##### Changing scaling

Certain variables, particularly biological parameters, are best displayed with a logarithmic colour scale. The spacing of the colour scale can be toggled between linear and logarithmic using the drop-down box to the right of the colour bar. Note that you cannot select a logarithmic scale if the colour scale range contains negative or zero values.

#### Creating animations

To create an animation, click the filmstrip button below the map. This will bring up a wizard allowing you to select the start and end times, followed by the number of frames to generate and the frame rate for playback. This may take a long time to generate, depending on the number of frames and the data resolution. Once playing, the animation can be stopped by clicking the same button.

#### Vertical sections and transects along arbitrary paths

At the top of the map itself, select the icon that looks like a line joining four points. Click on the map to start drawing a line. Add "waypoints" along this line by single-clicking at each point. Double-click to finish the line. A pop-up will appear showing the variation of the viewed variable along the line (i.e. a transect plot). If the variable has a vertical dimension, a vertical section plot will appear under the transect plot.

#### Changing the background map

A selection of background maps are available on which data can be projected. Select a different background map by clicking the small plus sign in the top right-hand corner of the interactive map.

#### Changing the map projection

The map projection is changed by selecting a new background map as above. If the background map is in a different projection then the data overlay will be automatically reprojected into the new coordinate system. Commonly, Godiva3 websites will give the option to select a background map in north or south polar stereographic projection. There may be a delay before the map appears in the new projection.

#### Saving and emailing the view

You may wish to save the current view to return to it later or share it with a colleague. The "Permalink" under the bottom right-hand corner of the map links to a complete URL that, when loaded, recreates the current view. Left-click on the permalink to bring up a new window with an identical view. Right-click on the permalink and select "Copy link location" or the equivalent for your web browser. You can then paste the link into a report, your notes or an email. You can also click on "email" (next to the permalink) to start a new email message in your default email client with the permalink already included in the message body.

#### Exporting to a map

By clicking the "Export to PNG" link below the map, a static image of the current view will be generated, which can be saved from your browser.

## Development

### Adding new colour palettes

To add new colour palettes to ncWMS, you must create palette files. These are text files with the extension ".pal". The name of the file will be the name of the palette. This files must contain one line for each colour in the palette. Intermediate colours will be interpolated. A colour is either of the form `#RRGGBB` or `#AARRGGBB` . Where values are in hexadecimal notation.

If the resulting palette files are placed in a directory named `$WEBAPP_DIR/WEB-INF/classes/palettes` then they will be picked up automatically (after a servlet restart) but may be deleted upon webapp redeployment.

A better solution is to place the palette files in a directory of your choice and modify `$WEBAPP_DIR/WEB-INF/classes/config.properties` . The property named `paletteDirs` should contain a comma-separated list of directories containing palette files, and all palettes will become available after a servlet restart.

Note that once a palette file has been defined this will add 2 available palettes - the normal one you have defined along with its inverse.

### Defining new style templates

To create a new style for plotting, you will need to create an SLD template. The documentation for SLD can be found in the root directory of the source code, and is named "ncWMS-sld_specification". Within this template, you may use the following placeholders:

*   $layerName
*   $paletteName
*   $scaleMin
*   $scaleMax
*   $logarithmic
*   $numColorBands
*   $bgColor
*   $belowMinColor
*   $aboveMaxColor
*   $opacity

In addition to the $layerName parameter, you may add children of the named layer, according to the role they have to their parent layer. For example, if a named layer has two children with roles "mag" and "dir" (this is the case for a vector layer), you may specify them as `$layerName-mag` and `$layerName-dir` . This style will then be supported only for parent layers with such children.

For further examples, see the existing style templates in the edal-graphics module at src/main/resources/styles.

Once you have created these templates, you may either place them in `$WEBAPP_DIR/WEB-INF/classes/styles`, or modify `$WEBAPP_DIR/WEB-INF/classes/config.properties` to specify the `styleDirs` property. This takes a comma-separated list of directories which will be scanned on startup and any defined styles residing within them will be made available.

### Adding new data readers

By default ncWMS supports reading of gridded NetCDF/GRIB/OPeNDAP data, and in-situ data from the EN3/4 UK Met Office dataset. To read additional types of data, a new data reader must be written. To do this, you must extend the class `uk.ac.rdg.resc.edal.dataset.DatasetFactory`. This has a single abstract method which returns a `uk.ac.rdg.resc.edal.dataset.Dataset` object given an ID and location.

The full details of how to implement this are beyond the scope of this guide, but it is recommended to example the two existing data readers: `uk.ac.rdg.resc.edal.dataset.cdm.CdmGridDatasetFactory` and `uk.ac.rdg.resc.edal.dataset.cdm.En3DatasetFactory`, which can be found in the edal-cdm module. The [EDAL Javadocs](apidocs/index.html) are reasonably complete and will be of great use. Additionally you may wish to contact the developers of ncWMS through the website, who will be happy to provide guidance and assistance.

## Authors and Contributors

[@guygriffiths](https://github.com/guygriffiths), [@jonblower](https://github.com/jonblower)