# Salute Report Android App

[![Build Status](https://travis-ci.com/chesapeaketechnology/android-salute-report.svg?branch=develop)](https://travis-ci.com/github/chesapeaketechnology/android-salute-report)
[![License](https://img.shields.io/badge/license-Apache%202-green.svg?style=flat)](https://github.com/chesapeaketechnology/android-salute-report/blob/develop/LICENSE)

The Salute Report Android App allows for the creation of SALUTE reports.  It walk the user through the process of creating a SALUTE report, and then writes the report to a file.

The SALUTE reports are stored in individual files with the `.salutereport` file extension. The file 
contents are written in JSON which makes it easy for other systems to consume the reports. If Sync
Monkey is also installed on the phone, the SALUTE reports are sent over to Sync Monkey so they can be 
synced to the cloud.

## Getting Started

To build and install the project follow the steps below:

    1) Clone the repo.
    2) Open Android Studio, and then open the root directory of the cloned repo.
    3) Connect an Android Phone (make sure debugging is enabled on the device).
    4) Install and run the app by clicking the "Play" button in Android Studio.

### Prerequisites

Install Android Studio to work on this code.

## Changelog

##### [0.1.2](https://code.ctic-inc.com/android-salute-report/files/fa2ccc5bddaf096177aeaa1fde7818cef94b18c4/?at=v0.1.2) - 2020-11-05
 * Fixed a bug where the app would crash when clicking next on the location screen when no location is selected.

##### [0.1.1](https://code.ctic-inc.com/android-salute-report/files/fa2ccc5bddaf096177aeaa1fde7818cef94b18c4/?at=v0.1.1) - 2020-05-06
 * SALUTE Reports are now sorted in order of date created.
 * Fixed a bug where the current location would not be displayed on the map when creating a SALUTE Report.
 * Fixed a bug where a SALUTE Report title would center on screen if it was longer than one line.
 * SALUTE reports are now shared as human-readable text files.
 * Fixed a bug where the SALUTE Report app crashed when switching between portrait and landscape mode.
 * Added a scroll bar to the SALUTE Report screens.
 * Fixed a bug where the "Equipment" text was cutoff on the Report Details view when using a lower resolution setting.
 * Added support for sharing Salute Reports.

##### [0.1.0](https://code.ctic-inc.com/android-salute-report/files/fa2ccc5bddaf096177aeaa1fde7818cef94b18c4/?at=v0.1.0) - 2020-04-03
 * Initial cut of the SALUTE Report Android App.

## Contact

* **Christian Rowlands** - [Craxiom](https://github.com/christianrowlands)
