package sample;

public class PassengerQueue {

    Node first;   //head of the list
    Node last;     //tail of the list

    //creating a constructor
    public PassengerQueue()
    {
        this.first = this.last = null;
    }


    public void add(Passenger key)
    {

        Node temp = new Node(key);

        if (this.last == null) {
            this.first = this.last = temp;
            return;
        }
        this.last.next = temp;
        this.last = temp;
    }

    public Node remove()
    {
        if (this.first == null)
            return null;

        Node temp = this.first;
        this.first = this.first.next; //link first node with the second node

        if (this.first == null)
            this.last = null;

        return temp;
    }
}

//LINKED LIST NODE
class Node {
    Passenger key;
    Node next;

    //constructor to create a new node
    public Node(Passenger key)
    {
        this.key = key;
        this.next = null;
    }
}



