# WikiData-DBpedia-Summarizer

**Aim of the Project**
The aim of the project is to provide an entity summarizer tool that extracts information about an entity from Wikidata and DBpedia
and finds the common relations between the two Knowledge Bases. The assumpition behind this model is the following one: *Data that 
is present in both the KB is probably more important than any other data*. We use different similarity measures (like cosine similarity) and
ontological property matching to extract the *common* relation between the two KB.

**Components** 
The system is built using the JADE agent system http://jade.tilab.com/. Using JADE you can create *intelligent* agents that can have a custom behaviour. We used agent to query the KB and to analyze the data retrived.

**Frontend** 
The frontend of this project was built using another framework (for curiosity). You can find it here: https://github.com/vinid/WikiData-DBpedia-Summarizer-Frontend
