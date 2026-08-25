# Console UI test plan

The test runner compiles the program and starts a fresh session for each case. Expected output is exact apart from Windows/Linux line-ending differences.

## Test Case: add, list, and exit

**Aim:** Verify that a todo can be added, listed, and retained until the user exits.

### Input

```text
todo read book
list
bye
```

### Expected output

```text
____________________________________________________________
 _       ___     _                         
| |     / / |__ (_)_ __ ___  ___ _   _     
| | /| / /| '_ \| | '_ ` _ \/ __| | | |    
| |/ |/ / | | | | | | | | | \__ \ |_| |    
|__/|__/  |_| |_|_|_| |_| |_|___/\__, |    
                                  |___/     
 ____        _   
| __ )  ___ | |_ 
|  _ \ / _ \| __|
| |_) | (_) | |_ 
|____/ \___/ \__|

Hello! I'm Whimsy Bot.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: task types and status changes

**Aim:** Verify deadline and event formatting, plus marking and unmarking a task.

### Input

```text
deadline return book /by Friday
event project meeting /from Monday 2pm /to 4pm
mark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 _       ___     _                         
| |     / / |__ (_)_ __ ___  ___ _   _     
| | /| / /| '_ \| | '_ ` _ \/ __| | | |    
| |/ |/ / | | | | | | | | | \__ \ |_| |    
|__/|__/  |_| |_|_|_| |_| |_|___/\__, |    
                                  |___/     
 ____        _   
| __ )  ___ | |_ 
|  _ \ / _ \| __|
| |_) | (_) | |_ 
|____/ \___/ \__|

Hello! I'm Whimsy Bot.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Friday)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Monday 2pm to: 4pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Friday)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [D][ ] return book (by: Friday)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Friday)
2.[E][ ] project meeting (from: Monday 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: delete a task

**Aim:** Verify that deleting a task removes it from the list, shifts the remaining tasks up, and reports the new task count.

### Input

```text
todo read book
deadline return book /by Friday
event project meeting /from Monday 2pm /to 4pm
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _       ___     _                         
| |     / / |__ (_)_ __ ___  ___ _   _     
| | /| / /| '_ \| | '_ ` _ \/ __| | | |    
| |/ |/ / | | | | | | | | | \__ \ |_| |    
|__/|__/  |_| |_|_|_| |_| |_|___/\__, |    
                                  |___/     
 ____        _   
| __ )  ___ | |_ 
|  _ \ / _ \| __|
| |_) | (_) | |_ 
|____/ \___/ \__|

Hello! I'm Whimsy Bot.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Friday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Monday 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: Friday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Monday 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: error handling for invalid input

**Aim:** Verify that Whimsy Bot reports a clear error instead of crashing on an unrecognised command, a task added with no description, a deadline/event missing its date markers, and an out-of-range or non-numeric mark/delete index — and that it keeps working normally afterwards.

### Input

```text
blah
todo
deadline return book
event project meeting
mark 5
mark abc
delete 5
delete abc
todo read book
list
bye
```

### Expected output

```text
____________________________________________________________
 _       ___     _                         
| |     / / |__ (_)_ __ ___  ___ _   _     
| | /| / /| '_ \| | '_ ` _ \/ __| | | |    
| |/ |/ / | | | | | | | | | \__ \ |_| |    
|__/|__/  |_| |_|_|_| |_| |_|___/\__, |    
                                  |___/     
 ____        _   
| __ )  ___ | |_ 
|  _ \ / _ \| __|
| |_) | (_) | |_ 
|____/ \___/ \__|

Hello! I'm Whimsy Bot.
What can I do for you today?
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! Please specify a deadline, e.g. 'deadline return book /by Sunday'.
____________________________________________________________
____________________________________________________________
OOPS!!! Please specify the event's start and end, e.g. 'event project meeting /from Monday 2pm /to 4pm'.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 5 in your list.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 5 in your list.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
