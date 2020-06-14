import numpy as np
import re


visited = []
trace = []
has_circle = False
circles = []
papers = {}
years = {}


def find_circle(adj):
    V = adj.shape[0]
    for i in range(V):
        if not i in visited:
            dfs(adj, i)


def dfs(adj, idx):
    global has_circle
    if idx in visited:
        if idx in trace:
            has_circle = True
            trace_index = trace.index(idx)
            circle = []
            for i in range(trace_index, len(trace)):
                circle.append(trace[i])
            circles.append(circle)
            return
        return
 
    visited.append(idx)
    trace.append(idx)
 
    if np.sum(adj[idx]) > 0:
        children = np.where(adj[idx, :] == 1)[0]
        for child in children:
            dfs(adj, child)
    trace.pop()


def is_circle(circle, adj):
    length = len(circle)
    for i in range(length):
        if adj[circle[i]][circle[(i+1)%length]] != 1:
            return False
    
    return True


def cut_loops(adj):
    find_circle(adj)
    for circle in circles:
        if not is_circle(circle, adj):
            continue
        length = len(circle)
        flag = False
        for i in range(length):
            if years[circle[i]] < years[circle[(i+1)%length]]:
                adj[circle[i]][circle[(i+1)%length]] = 0
                flag = True
                break
        if flag:
            continue
        for i in range(length):
            if years[circle[i]] == years[circle[(i+1)%length]]:
                adj[circle[i]][circle[(i+1)%length]] = 0
                break
    
    return adj


if __name__ == "__main__":
    adj = np.load('npy/after_cut_loop_1.npy')
    find_circle(adj)
    print(circles)
    exit()
    content = './cora/cora.content'
    info = './cora/papers'

    n_papers = 1
    with open(content, 'r') as f:
        lines = f.readlines()
        for line in lines:
            attrs = line.split('\t')
            paper_id = attrs[0]
            idx = n_papers
            papers[paper_id] = idx
            n_papers += 1
    num = 0
    with open(info, 'r') as f:
        lines = f.readlines()
        for line in lines:
            paper_id = line.split('\t')[0]
            if not paper_id in papers.keys():
                continue
            num += 1
            idx = papers[paper_id]
            r = re.search('<year> (.*) </year>', line)
            year = 0
            if r is not None:
                year = r.group(1)
                # print(year)
                i = 0
                while year[i] != '1':
                    i += 1
                year = int(year[i: i + 4])
            years[idx] = year
    
    print(adj.sum())
    adj = cut_loops(adj)
    print(adj.sum())
    np.save('npy/after_cut_loop_2.npy', adj)