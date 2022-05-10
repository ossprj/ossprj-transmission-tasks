# Reseed

Reseed torrents in Transmission using a combination of Predicate(s) and Comparator(s) to filter and prioritize torrents

## Configuration

The task can be configured via a YAML file name "application.yml"

- endpoint: This is the endpoint of the Transmission RPC Api
- maxActiveTorrents: Maximum number of torrents that can be running in this instance
- dryRun: If true, will do everything except the call to start each torrent. Defaults to false.

### Predicates

Predicates are used to filter the set of torrents to reseed

- LeecherCount (min,max)
- SeederCount (min,max)

min specifies a minimum count and must be >= 0

max specifies a maximum count and must be >= 0

### Comparators

Comparators are used to sort/order the set of torrents to reseed

- LeecherCount (sortOrder)
- SeederCount (sortOrder)

sortOrder can either be ASCENDING or DESCENDING

### Strategy

Strategy consists of:

- List Of "Predicates" (logically AND together)
- List of "Comparators" (evaluated in sequential order)

## Examples

All Strategies should probably have at minimum a LeecherCount predicate with a min of 1. 
Otherwise, there is no reason to reseed a torrent.

### With Leechers
    # This is the most basic Strategy
    # It will restart any stopped/paused torrents with at least one leecher

    reseedTorrent:
      endpoint: http://192.168.11.21:8084/transmission/rpc
      maxActiveTorrents: 10
      dryRun: true

      predicates:
        - name: LeecherCount
          config:
            minimum: 1

### With No Seeders
    # This Strategy only seeds torrents with 0 seeds and at least 1 leecher

    reseedTorrent:
      endpoint: http://192.168.11.21:8084/transmission/rpc
      maxActiveTorrents: 10
      dryRun: true

      predicates:
        - name: LeecherCount
          config:
            minimum: 1
        - name: SeederCount
          config:
            maximum: 0


### Most Popular
    # This strategy prioritizes torrents with the highest number of leechers

    reseedTorrent:
      endpoint: http://192.168.11.21:8083/transmission/rpc
      maxActiveTorrents: 10
      dryRun: true

      predicates:
        - name: LeecherCount
          config:
            minimum: 1

      comparators:
        - name: LeecherCount
          config:
            sortOrder: DESCENDING

### Most Popular With No Seeders
    # This Strategy only seeds torrents with 0 seeds and at least 1 leecher
    # Additionally, it prioritizes torrents with the highest number of leechers

    reseedTorrent:
      endpoint: http://192.168.11.21:8084/transmission/rpc
      maxActiveTorrents: 10
      dryRun: true

      predicates:
        - name: LeecherCount
          config:
            minimum: 1
        - name: SeederCount
          config:
            maximum: 0

      comparators:
        - name: LeecherCount
          config:
            sortOrder: DESCENDING

### Fairest Distribution
    # This strategy attempts to reseed in the fairest/broadest way possible
    # It prioritizes torrents with the lowest number of seeders and the highest number of leechers

    reseedTorrent:
      endpoint: http://192.168.11.21:8083/transmission/rpc
      maxActiveTorrents: 10
      dryRun: true

      predicates:
        - name: LeecherCount
          config:
            minimum: 1

      comparators:
        - name: SeederCount
          config:
            sortOrder: ASCENDING
        - name: LeecherCount
          config:
            sortOrder: DESCENDING



## Logging

Logging levels can be overridden in the application-XXX.yml

The default root logging level is WARN

    logging:
      level:
        root: WARN
        com.ossprj.transmission.task: DEBUG

## Command Line

Ensure the application.yml is in the same directory from which you run the jar

    java -jar ossprj-transmission-tasks-reseed-x.y.z.jar

Alternatively you can specify a "Spring Profile" to control which application-XXX.yml file is used as the configuration file. The following:

    java -Dspring.profiles.active=WithLeechers -jar ossprj-transmission-tasks-reseed-x.y.z.jar

will use the configuration from the application-WithLeechers.yml file
