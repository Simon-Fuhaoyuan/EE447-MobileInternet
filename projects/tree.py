import numpy as np
from numpy import linalg as la
import pandas as pd

A = np.load('npy/after_cut_loop_2.npy')
LEN = A.shape[0]

for i in range(A.shape[0]):
    if np.sum(A[i]) == 0: # if i does not cite, cite 0
        A[i, 0] = 1

W = A.copy()

D = np.zeros_like(W)
for i in range(W.shape[0]):
    D[i, i] = np.sum(W[i])
D_ = np.diag(np.diag(D) ** (-1/2))

L_regular = D_ @ (D - W) @ D

v, Q = la.eig(L_regular)

d = np.zeros((LEN, LEN))
for i in range(d.shape[0]):
    if i % 100 == 0:
        print(i) 
    for j in range(d.shape[1]):
        d[i, j] = la.norm(Q[i] - Q[j])

MaxDistance = np.max(d * A)

# Affinite table
At = [
    list(np.where(A[i] != 0)[0])
    for i in range(LEN)
]

# Dijkstra with heap
import heapq

# init
dis = d.copy()
dis[A == 0] = np.inf
step = A.copy()
step[A == 0] = np.inf

# for each node
for i in range(LEN):
    if i % 100 == 0:
        print(i)
    
    h = []
    # add all nodes to heap
    for v in range(LEN):
        heapq.heappush(h, (dis[i, v], step[i, v], v))
        
    # optimize
    while len(h) > 0:
        du, su, u = heapq.heappop(h)
        
        for v in At[u]:
            if du + dis[u, v] < dis[i, v]:
                dis[i, v] = du + dis[u, v]
                step[i, v] = su + step[u, v]

AverageStep = step[step != np.inf].mean()

R = np.zeros((LEN, LEN))
for i in range(R.shape[0]):
    if i % 100 == 0:
        print(i)
    for j in range(R.shape[1]):
        
        j_k = np.where(A[j] != 0)[0]
        for k in j_k:
            if dis[i, k] == np.inf:
                R[i, j] += MaxDistance * AverageStep 
            else:
                R[i, j] += dis[i, k]

R_network = np.zeros((LEN,))
for i in range(R_network.shape[0]):
    R_network[i] = np.sum(R[i]) - R[i, i]

delta_R = np.zeros((LEN, LEN))
for i in range(delta_R.shape[0]):
    for j in range(delta_R.shape[1]):
        delta_R[i, j] = np.abs(
            R_network[i] - R_network[j]
        )

u = np.where(A != 0)[0]
v = np.where(A != 0)[1]
edges = [(u[i], v[i]) for i in range(len(u))]

total_edge_num = len(edges)
delta_R_edges = []
for u, v in edges:
    delta_R_edges.append((delta_R[u, v], u, v))

delta_R_edges.sort(reverse=True)

tree = A.copy()
deleted_edge_num = 0
for delta_R_uv, u, v in delta_R_edges:
    if np.sum(tree[u]) > 1:
        tree[u, v] = 0
        deleted_edge_num += 1
        
    if LEN == total_edge_num - deleted_edge_num: # tree
        print('tree extracted')
        break

level = 0
this_level = [0]
visited = set([0])
while len(this_level):
    print(level, len(this_level), '\n', this_level, '\n\n\n\n')
    
    s = set()
    for v in this_level:
        next_level = set(np.where(tree[:, v] == 1)[0]) - visited
        s |= next_level
        
    visited |= s
    this_level = list(s)
    level += 1

np.save('npy/tree_.npy', tree)