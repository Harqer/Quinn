{
  description = "Lyria development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config = {
            allowUnfree = true;
            android_sdk.accept_license = true;
          };
        };

        androidComposition = pkgs.androidenv.composeAndroidPackages {
          buildToolsVersions = [ "35.0.0" ];
          platformVersions = [ "35" ];
          abiVersions = [ "x86_64" ];
          includeNDK = false;
          includeSystemImages = false;
          includeEmulator = false;
        };
        
        androidSdk = androidComposition.androidsdk;

      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = [
            pkgs.nodejs_20
            pkgs.jdk21
            androidSdk
          ];

          shellHook = ''
            export JAVA_HOME="${pkgs.jdk21}"
            export ANDROID_HOME="${androidSdk}/libexec/android-sdk"
            export ANDROID_SDK_ROOT="${androidSdk}/libexec/android-sdk"
            export PATH="$ANDROID_HOME/platform-tools:$PATH"
            
            echo "🚀 Mave Studio dev environment activated."
            echo "Node version: $(node -v)"
            echo "Java version: $(java -version 2>&1 | head -n 1)"
            echo "Android SDK: $ANDROID_HOME"
          '';
        };
      }
    );
}
