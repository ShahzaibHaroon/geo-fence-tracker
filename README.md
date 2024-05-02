# Geofence Manager Documentation

## Introduction
Geofence Manager is a mobile application designed to facilitate the setup, testing, and management of geofences. Geofence Manager allows users to define geographical boundaries, track entry and exit events, calculate dwell time within geofenced areas, and manage location permissions. This documentation provides an overview of the application's functionality, code structure, and usage.

## Permissions Flow
### LocationPermissionHandler
- Manages the permissions flow for accessing location services.
- Handles scenarios where users grant or deny location permissions.
- Provides clear explanations and guidance to users on the importance of enabling location services.
- Utilizes `requestLocationPermissions()` to request location permissions from the user.
- Displays a dialog prompting users to open settings if permissions are denied.

## Geofence Setup
### MainActivity
- Main entry point of the application.
- Responsible for checking if location permissions have been granted previously.
- Requests location permissions from the user if not previously granted.
- Upon permission grant, launches the MapsActivity for geofence setup.

### MapsActivity
- Integrates Google Maps for geofence setup.
- Allows users to set up geofences by tapping on the map.
- Provides a slider to adjust the radius of the geofence.
- Utilizes GeofenceHelper to handle geofence creation and management.
- Adds markers and circles on the map to visualize geofence boundaries.

### GeofenceHelper
- Handles geofence-related computations and interactions.
- Constructs Geofence objects based on user-defined parameters.
- Manages GeofencingClient for adding geofences.
- Provides methods to retrieve GeofencingRequest and PendingIntent.

## Geofence Testing
### GeofenceBroadcastReceiver
- Broadcast receiver responsible for handling geofence transition events.
- Captures entry and exit timestamps when the device enters or exits a geofence.
- Calculates dwell time within geofenced areas.
- Logs geofence-related events and durations for testing and debugging purposes.

## Testing
- The application was primarily tested using an emulator.
- Emulator settings were configured to simulate location updates and movement.
- Various scenarios were tested, including entering, exiting, and dwelling within geofenced areas.
- Logs from the GeofenceBroadcastReceiver were monitored to verify the accuracy of geofence events and calculations.