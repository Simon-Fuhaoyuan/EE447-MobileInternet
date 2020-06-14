import numpy as np
from tqdm import tqdm
from math import log


def get_subtree(tree_adj):
    sub_tree = np.zeros_like(tree_adj)
    sub_tree = get_idx_subtree(sub_tree, tree_adj, 0)

    for i in range(sub_tree.shape[0]):
        sub_tree[i][i] = 1    

    return sub_tree

def get_idx_subtree(sub_tree, tree_adj, idx):
    if np.sum(tree_adj[:, idx]) == 0:
        sub_tree[idx, idx] = 1
        return sub_tree

    children = np.where(tree_adj[:, idx] == 1)[0]
    for i in children:
        if i == idx:
            continue
        if sub_tree[i, i] == 0:
            sub_tree = get_idx_subtree(sub_tree, tree_adj, i)
        sub_tree[idx, np.where(sub_tree[i, :] == 1)[0]] = 1
    
    sub_tree[idx, idx] = 1
    
    return sub_tree

def tree_entropy(init_adj, tree_adj):
    sub_tree = get_subtree(tree_adj)
    V_max = init_adj.shape[0]
    m = np.sum(init_adj)
    entropy = np.zeros(V_max)
    num = 0
    for a in range(1, V_max):
        V = V_max
        parent_idx = np.where(tree_adj[a, :] == 1)[0]
        if len(parent_idx) > 0:
            for idx in parent_idx:
                if idx != a:
                    V = np.sum(sub_tree[idx, :])
                    break
        V_a = np.sum(sub_tree[a, :])
        if V == 1 and V_a == 1:
            num += 1
        idx = np.where(sub_tree[a, :] == 1)[0]
        idx_set = set(idx)
        g_a = 0
        for i in idx:
            edge_idx = set(np.where(init_adj[i, :] == 1)[0]) - idx_set
            g_a += len(edge_idx)
            edge_idx = set(np.where(init_adj[:, i] == 1)[0]) - idx_set
            g_a += len(edge_idx)
        entropy[a] = -1 * g_a / (2 * m) * log(V_a/V)
    # print(num)
    return entropy


def inter_knowledge_entropy(init_adj, tree_adj):
    sub_tree = get_subtree(tree_adj)
    V_max = init_adj.shape[0]
    m = np.sum(init_adj)
    entropy = np.zeros((V_max, V_max))
    for i in range(V_max):
        children = np.where(tree_adj[:, i] == 1)[0]
        for j in range(len(children) - 1):
            for k in range(j + 1, len(children)):
                a = children[j]
                b = children[k]
                V_a = np.sum(sub_tree[a])
                V_b = np.sum(sub_tree[b])
                V = np.sum(sub_tree[i])
                g_ab = 0
                sub_children = set(np.where(sub_tree[a] == 1)[0]) | set(np.where(sub_tree[b] == 1)[0])
                for idx in sub_children:
                    edge_idx = set(np.where(init_adj[idx, :] == 1)[0]) - sub_children
                    g_ab += len(edge_idx)
                    edge_idx = set(np.where(init_adj[:, idx] == 1)[0]) - sub_children
                    g_ab += len(edge_idx)
                entropy[a][b] = -1 * g_ab / (4 * m) * log(V_a * V_b / V ** 2)
                entropy[b][a] = entropy[a][b]
    
    return entropy


def point_entropy(init_adj, tree_adj):
    size = init_adj.shape[0]
    entropy = np.zeros(size)
    H = tree_entropy(init_adj, tree_adj) # Tree entropy
    I = inter_knowledge_entropy(init_adj, tree_adj) # Inter entropy
    for a in range(size):
        entro = H[a]
        children = np.where(tree_adj[:, a] == 1)[0]
        for a_i in children:
            entro -= H[a_i]
        for i in range(len(children) - 1):
            for j in range(i + 1, len(children)):
                a_i = children[i]
                a_j = children[j]
                entro += I[a_i][a_j]
        entropy[a] = abs(entro)

    return entropy


def test(a):
    a[0] = 1

if __name__ == "__main__":
    tree = np.load('npy/tree_.npy')
    init_adj = np.load('npy/init_adj.npy')
    sub_tree = get_subtree(tree)
    # print(np.sum(sub_tree[0, :]))
    # entropy = point_entropy(init_adj, tree)