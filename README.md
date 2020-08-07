# MapPuller
Asynchronous Map Puller for GitHub hosted maps.

## Configuration

```
source:
  mapnames:
    - Castles
  target: "Maps"
  repo: "WarzoneMC/Maps"
  auth_token: ""
```
#### mapnames
List of the map names to pull from the repository.

#### target
The folder that the pulled maps go into.

#### repo
Also known as repository, the maps that the plugin pull from.

#### auth_token
The authentication token that MapPuller will use to pull maps from.
To see how to create a personal access token read [GitHub's Guide on creating a personal access token](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token)
> Example auth_token: "username:token"
