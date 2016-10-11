#.apcsp file type content specification.
Version 1.0.0
Last updated 10/10/2016, 6:13 P.M. PST
---
##Table of contents
**1** Introduction<br>
[1.1](#11-conventions-used-in-this-document)Conentions used in this document<br>
[1.2](#12-introduction)Introduction to apcsp<br>
**2** Actual syntax<br>
[2.1](#21-encoding-and-charcter-sets)Encoding and charcter sets<br>
[2.2](#22-color-seperation)Color Seperation<br>
[2.3](#23-colors)Colors<br>
[2.4](#24-comments)Coments<br>
[2.5](#25-minified-files)Minified Files<br>
**3** Examples
[3.1](#31-example-apcsp-file).apcsp example<br>
[3.2](#32-example-minapcsp-file).min.apcsp example<br>
---
###1.1 Conventions used in this document
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL
NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED",  "MAY", and
"OPTIONAL" in this document are to be interpreted as described in
[RFC 2119](https://tools.ietf.org/html/rfc2119).

The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL
NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED",  "MAY", and
"OPTIONAL" are case insensitive, meaning that "May", "may", and "mAy" all have the same meaning as "MAY".
##1.2 Introduction
.apcsp is a file type used for defining palettes used in the [APCS Graphics Libary](apcs.io)
A palette is simply a list of colors, and a color is simply the link between a name and a RGB value.
##Section 2 - Actual syntax
##2.1 Encoding and Charcter Sets
All files ending in .apcsp must be encoded in UTF-8 using the Unicode charcter set.
##2.2 Color Seperation
A color is the link between a name and a RGB value. Each color shall be seperated by a semicolon, and may also contain a line break after a semicolon.
##2.3 Colors
Each color shall the name, then the red value, then the green value, then the blue value, where each these items are seperated by a comma. The name shall only contain lowercase letters, numbers, and spaces. Each red, green, or blue value shall be greater than or equal to 0, and less than or equal to 255. A completed color block should look like this:<br>
`color name`,`red value`,`blue value`,`green value`<br>
An example is:<br>
`blue,0,255,0`
#2.4 Comments
Comments are not required.
A comment is any block of text that starts with an `/*` and ends at the first `*/` in the block of text that can span more than one line. All comments are ignored by the compiler and can be used to help other people understand your colors.

##2.5 Minified Files
All .apcsp files do not need to follow this section
A minified file uses the file format .min.apcsp and follows the same specifications, and also this section.
All minified apcsp files must not have any spaces other than spaces inside color names, and must not have any line breaks. Comments are optional.
##3.1 Example .apcsp file:
```
/*
 * this is a comment
*/
blue,0,0,255;
brown,139,69,19;
green,0,128,0;
light blue,173,216,230;
orange,255,165,0;
purple,128,0,128;
red,255,0,0;
red orange,255,69,0;
white,255,255,255;
yellow,255,255,0;
yellow green,154,205,50;
```
##3.2 Example .min.apcsp file
```
/*
 * this is a comment
*/
black,0,0,0;blue,0,0,255;brown,139,69,19;green,0,128,0;light blue,173,216,230;orange,255,165,0;purple,128,0,128;red,255,0,0;red orange,255,69,0;white,255,255,255;yellow,255,255,0;yellow green,154,205,50;
```
