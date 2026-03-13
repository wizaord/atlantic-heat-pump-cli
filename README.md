# Atlantic Heat Pump CLI

A command-line tool to control Atlantic/Fujitsu heat pumps via the Atlantic Magellan cloud API.

## Features

- List all devices on your account
- Check device status (on/off, mode, current and target temperature, fan speed, swing/vane orientation)
- Turn devices on/off
- Switch mode (heat, cool, auto)
- Set target temperature
- Set fan speed (quiet, 2, 3, 4, auto)
- Set vane orientation (positions 1-4, swing/oscillation)

## Requirements

- Java 17+
- An Atlantic CozyTouch account (email/password)

## Build

```bash
./gradlew shadowJar
```

Produces `build/libs/atlantic-heat-pump-cli-1.0.0-all.jar`.

## Usage

### Authentication

Credentials can be provided via CLI options or environment variables:

```bash
# CLI options
atlantic-heat-pump --login user@example.com --password secret devices

# Environment variables
export ATLANTIC_LOGIN=user@example.com
export ATLANTIC_PASSWORD=secret
atlantic-heat-pump devices
```

### Commands

```bash
# List devices
atlantic-heat-pump devices

# Show device status
atlantic-heat-pump status
atlantic-heat-pump status --device <device-url>

# Power on/off
atlantic-heat-pump power on
atlantic-heat-pump power off --device <device-url>

# Set mode
atlantic-heat-pump mode heat
atlantic-heat-pump mode cool --device <device-url>
atlantic-heat-pump mode auto

# Set target temperature
atlantic-heat-pump temp --set 22.5
atlantic-heat-pump temp --set 20 --device <device-url>

# Set fan speed
atlantic-heat-pump fan quiet
atlantic-heat-pump fan 2
atlantic-heat-pump fan 3
atlantic-heat-pump fan 4
atlantic-heat-pump fan auto --device <device-url>

# Set vane orientation (swing)
atlantic-heat-pump swing 1        # position 1 (highest)
atlantic-heat-pump swing 2        # position 2
atlantic-heat-pump swing 3        # position 3
atlantic-heat-pump swing 4        # position 4 (lowest)
atlantic-heat-pump swing swing    # oscillating mode
atlantic-heat-pump swing 2 --device <device-url>
```

All commands that target a device accept `--device` / `-d`. If omitted, the first device found is used.

### Shell alias (optional)

```bash
alias atlantic-heat-pump='java -jar /path/to/atlantic-heat-pump-cli-1.0.0-all.jar'
```

## Architecture

```
domain/          Pure models (AcConfig, AcDevice, AcState, AcMode, FanSpeed, SwingMode) and port interface (AcPort)
infrastructure/  Magellan API client and port factory
cli/             Clikt commands (devices, status, power, mode, temp, fan, swing)
Main.kt          Composition root
```

## Tech stack

- Kotlin 2.3
- Clikt (CLI framework)
- Ktor (HTTP client/server)
- kotlinx.serialization (JSON)

## Tests

```bash
./gradlew test
```

Integration tests run against a fake Magellan server (in-process Ktor server).

## License

[MIT](LICENSE)
