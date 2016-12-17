# supremebot-java

Improves upon the concept of supremebot-py by providing a user interface via JavaFX. Users can add payment information and automate orders
to Supreme's online shop. All inputs are essentially routed to a Java implementation of the python script, which now depends on selenium's
Java bindings, as expected. Users can submit orders to a thread pool that allows concurrent orders and demands a specific time-to-fire for
each order.
