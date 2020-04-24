from scipy.special import comb

dp = {}

def main():
    k = 7
    X = 150
    p = 0.51
    E = 0.0
    for x in range(X + 1):
        tmp = (k + 2 * x + 2) * combinations(k + 1, x)
        tmp *= (p ** (k + x + 2))
        tmp *= ((1 - p) ** x)
        E += tmp
    print(E)

def combinations(ahead, X):
    '''
    ahead means how many blocks of the initial chain is ahead of the attacker's new chain
    x means how many blocks will be linked to the initial chain
    '''
    if X == 0:
        return 1

    has_key = False
    if ahead in dp.keys():
        has_key = True
        if X in dp[ahead].keys():
            return dp[ahead][X]
    
    res = 0
    for x in range(1, X + 1):
        if x > ahead + 1:
            break
        res += comb(ahead + 1, x) * combinations(2 * x - 1, X - x)
    
    if has_key:
        dp[ahead][X] = res
    else:
        dp[ahead] = {X: res}
    return res


if __name__ == '__main__':
    main()
