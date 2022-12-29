# TP 1 - Anagrams

Now, it's up to you to write your own Hadoop application.

## Objective

You are provided with a list of common English words (`common_words_en_subset.txt`).
We wish to pinpoint which words are anagrams of one another.

As a reminder, two words are anagrams if their letters are the same but in a
different order (such as « melon » and « lemon », for example).

> Note: you can use Hadoop Streaming (as seen during the course) if you feel more
> comfortable using another language instead of Java. The main purpose here is to
> implement an efficient MapReduce algorithm to solve the problem.

## Remarks

- Apply the map/reduce methodology as described during the course.
- As yourself: what should my key be ? What is the common factors between anagrams, and how could that help ?
- Do not forget that you will need to copy the input data file to HDFS for
processing.
