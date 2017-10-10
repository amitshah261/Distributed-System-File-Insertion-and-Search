# Distributed-System-File-Insertion-and-Search
Implemented File Search and File Insertion in a Distributed hash table implementation.

# About the project:
• Seven servers form a binary tree, in which a server is a node, but each node
NEVER uses pointers explicitly to remember its parent and children.  
• Instead, the jth node at tree level i for file f is identified by hash function H(f,
i, j). For example, H(f, 0, 0) is the root, and H(f, 1, 0) and H(f, 1, 1) are the
children of the root in the tree constructed for file f.  
• Different trees are built for different files based on the hash function.  
• File f is stored at the root initially, and is replicated toward the leaf nodes as
it becomes popular. Assume that f is popular once a node with f has received
requests for f five times.  
• A client issues a search request for f that always begins at a leaf node chosen
at random.  
• The request traverses toward the root until f is found.  
• When there is a search request, display the trail including where it starts and
where it finds f, and which nodes contain f.  
• You also need to create a mechanism to map hash values to servers in RIT CS lab.
Ensure that a particular hash value must be mapped to the same server all the
time.  
