from sklearn import manifold
import numpy as np
import matplotlib.pyplot as plt
import os


color = ['r', 'orange', 'y', 'g', 'b', 'm', 'k']

def tSNE(x, y, classes, n_components=2):
    tsne = manifold.TSNE(n_components=n_components, init='pca', random_state=501)
    X_tsne = tsne.fit_transform(x)

    print("Org data dimension is {}. Embedded data dimension is {}".format(x.shape[-1], X_tsne.shape[-1]))

    x_min, x_max = X_tsne.min(0), X_tsne.max(0)
    X_norm = (X_tsne - x_min) / (x_max - x_min)  # normalize
    plt.figure()
    
    for i, (cate) in enumerate(classes):
        X = X_norm[y == cate][: , 0]
        Y = X_norm[y == cate][: , 1]
        plt.scatter(X, Y, s=20, c=color[i], label=cate)
    
    plt.xticks([])
    plt.yticks([])
    plt.legend()
    plt.show()


if __name__ == "__main__":
    classes = {
        'Case_Based': 0,
        'Genetic_Algorithms': 1,
        'Neural_Networks': 2,
        'Probabilistic_Methods': 3,
        'Reinforcement_Learning': 4,
        'Rule_Learning': 5,
        'Theory': 6
    }
    papers = {}
    content = './cora/cora.content'
    cites = './cora/cora.cites'

    n_papers = 0
    y = []
    with open(content, 'r') as f:
        lines = f.readlines()
        for line in lines:
            attrs = line.split('\t')
            paper_id = attrs[0]
            idx = n_papers
            paper_class = attrs[-1][:-1] # delete '\n'
            y.append(paper_class)
            papers[paper_id] = (idx, classes[paper_class])
            n_papers += 1
    y = np.array(y)

    adj = np.zeros((n_papers, n_papers))
    with open(cites, 'r') as f:
        lines = f.readlines()
        for line in lines:
            citing = line.split('\t')
            if citing[0] in papers.keys() and citing[1][:-1] in papers.keys(): # make sure both paper in target_class
                cited_paper_idx = papers[citing[0]][0]
                citing_paper_idx = papers[citing[1][:-1]][0] # delete '\n'
                adj[citing_paper_idx][cited_paper_idx] = 1

    nums = 0
    out_degree = np.zeros((n_papers, n_papers))
    out_degree_norm = np.zeros((n_papers, n_papers))
    for i in range(n_papers):
        degree = adj[i].sum()
        if degree == 0:
            degree = 1
            nums += 1
        out_degree[i][i] = degree
        out_degree_norm[i][i] = degree ** (-0.5)
        
    Laplace = np.dot(out_degree_norm, (out_degree - adj))
    Laplace = np.dot(Laplace, out_degree_norm)
    print(Laplace.shape)
    
    tSNE(Laplace, y, list(classes.keys()))
