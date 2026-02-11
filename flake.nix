{
  description = "A very basic flake";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
  };

  outputs = { self, nixpkgs }:
  let
    system = "x86_64-linux";
    pkgs = import nixpkgs { inherit system; };
  in
  {
    devShells.${system}.default = pkgs.mkShell {
        buildInputs = with pkgs; [ jdk21 libxxf86vm xorg.libXtst glib gtk3 libGL ];
        shellHook = ''
            export LD_LIBRARY_PATH=${pkgs.lib.makeLibraryPath [ pkgs.xorg.libXtst pkgs.libxxf86vm pkgs.glib pkgs.gtk3 pkgs.libGL ]}:$LD_LIBRARY_PATH
        '';
      };
    };
}
