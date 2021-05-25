
# SwissBorg Arbitrage Exercise

## How to run

amm main.sc PARAM

where param can be 
- b -> runs both versions
- f -> runs only the functional one
- r -> runs only the reference one

## Solution

I'm presenting 2 different solutions of the arbitrage problem. Both are implementation of the Bellman Ford algorithm, but with different coding style and optimizations.

- The first one, called Reference, is inspired in this [page](https://www.thealgorists.com/Algo/ShortestPaths/Arbitrage) where you can find a purely procedural optimized implementation. It's optimized because it will only relax those vertices wich at least one of the inbound edges has gotten relaxed in the previous iteration. It also exits if a negative cycle was found.
- The second one, called Functional, it's a traditional implementation in a more "functional" style. It does not exit when a negative cycle is found, instead it runs V-1 times and then it checks if there is a negative cycle.


## Algorithmic complexity analysis

The Bellman-Ford Algorithm has a complexity if O(EV). 
The graph representing relationship between currencies are dense graph so E = O(V * V). So overall time complexity O(V3), where V = total number of vertices in the graph, E = total number of edges in the graph. V = total number different kinds of currencies in the given problem.

## CHSB Token

The SwissBorg token (CHSB) is an implementation of fungible token interface known in [ERC20](https://eips.ethereum.org/EIPS/eip-20) using the [Etoken2 Asset Proxy](https://github.com/Ambisafe/etoken2/blob/master/contracts/AssetProxy.sol). The token is not peg to any other currency and it was deployed to address [0xba9d4199fab4f26efe3551d490e3821486f135ba](https://etherscan.io/token/0xba9d4199fab4f26efe3551d490e3821486f135ba#readContract)

### Caracteristics

- It's not peg to any other token or currency
- It's not backed by venture capital
- The total maximum supply is 1,000,000,000 and the contract does not provide a mint function to create more token, nevertheless the underlying token can be changed to a one where the mint function exists letting the users to opt in/out the new address.
- As today it has a market cap of 748,790,395.00 USD

### Token Economics

- It can be staked at SwissBorg to become a premium or a community member. This special users get benefits from it like pay less commissions or get better yields. 
- Participate in the SwissBorg DAO when this one is released. I could not find a release date.
- There is a "Buy-Back and Burn" mechanism by SwissBorg where 20% of the ecosystem revenue is used to decrease the supply of the token.
- The token has a yield program in the platform where users are being rewarded daily
