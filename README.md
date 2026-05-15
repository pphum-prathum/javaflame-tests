# ITCS473 – Project Assignment 1 – ShuperShine

## Team
-  Saruta Nakro
-  Chalisa Kengkeawpennap
-  Phanphum Prathumsuwan
-  Pran Tantipiwatanaskul
-  Raweerot Bhasidhchirapiroch
-  Nimmida Maneewan

## Project Overview: Javaflame Extension

**Project Repository**: [Javaflame GitHub](https://github.com/beothorn/javaflame)

**Stars**: 92

**Programming Language**: Java

### Purpose:
This project extends the open-source [beothorn/javaflame](https://github.com/beothorn/javaflame) repository by developing additional unit test suites to systematically evaluate the parser and matcher components of the JavaFlame library. The aim is to enhance the test coverage, verify the robustness of error-handling mechanisms, and assess the effectiveness of the test design using formal input domain modeling and systematic test generation techniques.

### What is Javaflame?
Javaflame is a debugging tool written in Java, designed to capture function call arguments and display them visually as flame graphs. Unlike traditional profilers, which sample the execution stack to assess performance or memory usage, Javaflame injects bytecode on every function call that matches a specific filter. It collects the function name and argument values (via `toString()`) and stores this information on a separate stack for later visualization. It’s particularly useful for debugging and visualizing how logic is handled in code, especially when used with automated tests.

### Key Features:
- **Argument Value Display**: Shows argument values during function calls, not just the function signatures.
- **Flame Graphs**: Generates one flame graph per thread for clear visualization of function calls.
- **Instrumentation Filtering**: Filters classes for instrumentation, applying bytecode transformation only where necessary to minimize performance impact.
- **Exclusion of Classes/Packages**: Allows you to exclude certain classes or packages from instrumentation.
- **Continuous Snapshots**: Supports continuous snapshots for ongoing analysis.
- **HTML and JSON Output**: Provides results in a simple HTML file, with all captured data accessible in a JSON format (`data.js`).
- **Optional Start/Stop Recording**: Can start or stop recording based on the invocation of specific methods.

We designed and implemented **10 new test suites** targeting different functional areas of the JavaFlame parser:

- **`AgentMetadataBCCTest`** – validates metadata retrieval for initialized and uninitialized agent states using **Base Choice Coverage (BCC)**.
- **`ClassAndMethodMatcherMBCCTest`** – applies **Multiple Base Choice Coverage (MBCC)** to test class/method matching logic.
- **`DebugListenerACoCTest`** – validates file writing and exception handling behaviors using *All Combinations of Conditions (ACoC)*.
- **`ElementMatcherFromExpressionMBCCTest`** – validates expression-based element matching under **Multiple Base Choice Coverage (MBCC)**.
- **`FunctionCallRecorderECCTest`** – validate the class name of called method, either simple name or qualified name using **Each Choice Coverage (ECC)**
- **`FunctionCallRecorderWithValueCapturingBCCTest`** – verifies value-to-string conversion handling across null, array, and exception cases using *Boundary & Condition Coverage (BCC)*.
- **`MethodInstrumentationAgentPWCTest`** – validate if the method can read bytes and close itself appropriately using **Pair-Wise Coverage (PWC)**
- **`Parser_AllComb_NegGroupOpTest`** – applies **All-Combination Coverage (ACoC)** to negative and grouped operator scenarios.  
- **`Parser_Pairwise_MissingAndFlagsTest`** – applies **Pairwise Coverage (PWC)** for error-path and flag-handling cases.  
- **`TokenECCTest`** – tests equivalence partitions of token types using **Equivalence Class Coverage (ECC)**.

Each suite follows a structured process inspired by **input domain modelling** principles:

1. **Identify testable functions** – define the core method under test (e.g., `Parser.parse()` or `Lexer.tokenize()`).
2. **Characterize parameters and outputs** – document input types, expected return values, and exceptional behaviour.
3. **Model the input domain** – establish partitions and boundary conditions.
4. **Define test requirements** – combine input partitions using five combination strategies:  
   - **ACoC (All-Combinations Coverage)**  
   - **ECC (Equivalence Class Coverage)**  
   - **PWC (Pairwise Coverage)**  
   - **BCC (Base Choice Coverage)**  
   - **MBCC (Multiple Base Choice Coverage)**
5. **Derive and implement test values** – convert selected input combinations into concrete JUnit test cases.

By integrating both **interface-based** and **functionality-based** characteristics, this project demonstrates a systematic and measurable approach to white-box and black-box testing.  
The resulting tests improve the reliability and maintainability of JavaFlame’s parsing logic while serving as a teaching reference for coverage-based test design.

---


# SUITE 1 — ACoC
**Technique:** All Combinations of Conditions (ACoC)

## Test Suite: DebugListenerACoC_Test

### Step 1 – Identify Testable Function

```java
writeDebugFile()
```

### Purpose
This method writes debug information (class names) to a file.  
It supports three main operations:
- **Create mode:** creates a new file if it doesn’t exist
- **Append mode:** appends to an existing file
- **Error handling:** catches I/O exceptions to prevent crashes

---

### Step 2 – Identify Parameters, Return Types, and Exceptional Behavior

| Item | Description |
|------|--------------|
| **Parameters** | • `debugClassesToWrite`: set of class names to write <br>• `snapshotDirectoryAbsolutePath`: target directory <br>• `file`: file name |
| **Return Type** | `void` |
| **Normal Behavior** | Writes all class names to a new or existing file |
| **Exceptional Behavior** | If an I/O error occurs, it is caught internally (no crash) |
| **Testing Goal** | Verify that every possible logical combination of the internal conditions behaves correctly |

---
### Step 3 – Model the Input Domain

| ID | Description | Type |
|----|--------------|------|
| **C1** | Whether the set is empty | Interface-based |
| **C2** | Whether the file already exists | Interface-based |
| **C3** | Whether file writing succeeds | Functionality-based |

| ID | Characteristic | Type | b1 | b2 |
|----|----------------|------|----|----|
| **C1** | **Whether the set is empty** | *Interface-based* | Has data | No data | 
| **C2** | **Whether the file already exists** | *Interface-based* | File exists | No file |  
| **C3** | **Whether file writing succeeds** | *Functionality-based* | Error occurs | No error |  
---

### Step 4 – Combine Partitions to Define Test Requirements

| Test | C1 (Has Data?) | C2 (File Exists?) | C3 (Error Occurs?) |
|------|----------------|------------------|--------------------|
| **T1** | Has data | Has file | Error occurs |
| **T2** | Has data | Has file | No error |
| **T3** | Has data | No file | Error occurs |
| **T4** | Has data | No file | No error |
| **T5** | No data | Has file | Error occurs |
| **T6** | No data | Has file | No error |
| **T7** | No data | No file | Error occurs |
| **T8** | No data | No file | No error |

**Complete:** all logical cases included  
**Disjoint:** no overlapping conditions

**Summary:** There are **3 binary conditions (C1–C3)** → total **2³ = 8 combinations**.
---

### Step 5 – Derive Test Values and Expected Outputs  

| Test | Input Setup / Scenario | Expected Output |
|------|------------------------|-----------------|
| **T1** | `Set = {}` (empty) with valid path and target file `"file1.txt"` | Function does nothing; no file is created |
| **T2** | `Set = {"A","B"}` with new file `"newFile.txt"` in valid directory | File is created successfully and contains written data |
| **T3** | `Set = {"C1","C2"}` with existing file `"exist.txt"` already containing `"old\n"` | Data is appended successfully to `"exist.txt"` |
| **T4** | `Set = {"X"}` with invalid path `"Z:/invalid_path_does_not_exist"` | Exception is caught internally; no crash and no file created |
| **T5** | `Set = {"Y"}` with invalid directory `"?:/unreachable"` (no file yet) | Write operation fails; exception is caught internally; no file created |
| **T6** | `Set = {"L1","L2","L3"}` with existing file `"multi.txt"` containing `"line1\n"` | All entries are appended successfully; file length > 0 |
| **T7** | `Set = {"Hello"}` with valid empty directory path and file `"newDir.txt"` | File is created successfully in directory |
| **T8** | `Set = {"AAA","BBB"}` with existing file `"clear.txt"` | File is updated successfully and the input set becomes empty after write |


---

# SUITE 2 — ACoC


**Technique:** All Combinations of Conditions (**ACoC**)

**Test Suite:** `Parser_AllComb_NegGroupOpTest`


## Step 1 – Identify Testable Function

```java
Parser.parse(Deque<Token> tokens)
```

### Purpose

Builds an **AST** (Abstract Syntax Tree) from a token stream.
This suite focuses on logical-expression composition on the left operand and verifies that:

* **Binary operator** at the root is correct (`&&` or `||`)
* Left side respects **negation** (`!`) and **grouping** (`(...)`)
* Right side is a simple **atom** (`"bar"`)
* Parentheses are structural only (not present as AST nodes)

---

## Step 2 – Identify Parameters, Return Types, and Exceptional Behavior

| Item | Description |
| :--- | :--- |
| **Parameters** | `Deque<Token> tokens` (e.g., `["!","(","foo",")","&&","bar"]`) |
| **Return Type** | `ASTNode` (rooted at the chosen binary operator) |
| **Normal Behavior** | Produces AST with root operator; left subtree is either `STRING_VALUE` or `NOT(STRING_VALUE)`; right is `STRING_VALUE` |
| **Exceptional Behavior** | `CompilationException` for malformed inputs (not the goal of this suite) |
| **Testing Goal** | Validate all combinations of: **left negation**, **left grouping**, and **operator kind** |

---

## Step 3 – Model the Input Domain

### 1. Develop Characteristics

| ID | Description | Type |
| :--- | :--- | :--- |
| **C1** | Left side is negated (`!`) | Functionality-based |
| **C2** | Left side is grouped (`(...)`) | Functionality-based |
| **C3** | Operator is **AND** vs **OR** | Functionality-based |

### 2. Partition Characteristics

| Characteristic | b1 | b2 |
| :--- | :--- | :--- |
| **C1:** left negated | True | False |
| **C2:** left grouped | True | False |
| **C3:** operator | AND (`&&`) | OR (`||`) |

* **Complete:** all logical cases included
* **Disjoint:** no overlap between partitions

**Summary:** There are **3 binary characteristics** (C1–C3) $\rightarrow$ total $2^3 = 8$ combinations.

---

## Step 4 – Combine Partitions to Define Test Requirements

| Test | C1 (negated) | C2 (grouped) | C3 (op) | Scenario | Expected AST Shape |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **T1** | F | F | AND | `foo && bar` | Root: AND; Left: STRING; Right: STRING |
| **T2** | T | F | AND | `!foo && bar` | Root: AND; Left: NOT(STRING); Right: STRING |
| **T3** | F | T | AND | `(foo) && bar` | Root: AND; Left: STRING; Right: STRING |
| **T4** | T | T | AND | `!(foo) && bar` | Root: AND; Left: NOT(STRING); Right: STRING |
| **T5** | F | F | OR | `foo || bar` | Root: OR; Left: STRING; Right: STRING |
| **T6** | T | F | OR | `!foo || bar` | Root: OR; Left: NOT(STRING); Right: STRING |
| **T7** | F | T | OR | `(foo) || bar` | Root: OR; Left: STRING; Right: STRING |
| **T8** | T | T | OR | `!(foo) || bar` | Root: OR; Left: NOT(STRING); Right: STRING |

**Note:** Grouping uses `parseUntilClose(...)` but does not introduce paren nodes into the AST.

---

## Step 5 – Derive Test Values

| Test | Token Sequence (example) | Expected AST (essence) | Explanation |
| :--- | :--- | :--- | :--- |
| **T1** | `["foo","&&","bar"]` | `AND( "foo", "bar" )` | Baseline (no negation/grouping). |
| **T2** | `["!","foo","&&","bar"]` | `AND( NOT("foo"), "bar" )` | Exercises `parseUnaryOperand` on left. |
| **T3** | `["(","foo",")","&&","bar"]` | `AND( "foo", "bar" )` | Exercises `parseUntilClose` on left. |
| **T4** | `["!","(","foo",")","&&","bar"]` | `AND( NOT("foo"), "bar" )` | Negation + grouping together. |
| **T5** | `["foo","||","bar"]` | `OR( "foo", "bar" )` | **OR** operator baseline. |
| **T6** | `["!","foo","||","bar"]` | `OR( NOT("foo"), "bar" )` | Negation with **OR**. |
| **T7** | `["(","foo",")","||","bar"]` | `OR( "foo", "bar" )` | Grouping with **OR**. |
| **T8** | `["!","(","foo",")","||","bar"]` | `OR( NOT("foo"), "bar" )` | Negation and grouping with **OR**. |

---

# SUITE 3 — ECC

**Technique**: Each-Choice Coverage (ECC)  
**Test Suite**: TokenECCTest  
**File**: TokenECCTest.java  

### Step 1 – Identify Testable Function
- **Class**: com.github.beothorn.agent.parser.Token  
- **Methods Tested**: and(), string(String)  
- **Purpose**: Generate Token objects for expression parsing and verify equality behavior.

### Step 2 – Identify Parameters, Return Types, and Exceptional Behaviour

| Item            | Description                                        |
|-----------------|----------------------------------------------------|
| **Parameter**   | String value (only for `string(String)`)           |
| **Return Type** | Token                                              |
| **Exceptional Behaviour** | None — all factory methods return valid tokens |

### Step 3 – Model the Input Domain

| ID  | Characteristic      | Type             | b1                         | b2                         | Notes                          |
|-----|---------------------|------------------|----------------------------|----------------------------|--------------------------------|
| C1  | Factory method type | Interface-based  | Logical operator (`and`)    | String literal (`string(String)`) | Covers token creation form |
| C2  | Equality condition  | Functionality-based | Tokens with same type/value | Tokens with different type/value | Tests equality correctness |

#### Each Choice Coverage Formula:
***Total test cases*** = Max(Q i=1 (Bi)) |
***Total test cases*** = 2


#### Complete Disjointness:
- Each characteristic in the test case has distinct and non-overlapping values, ensuring complete disjointness. For example, the logical operator (`and()`) is distinct from the string literal (`string(String)`), and equality conditions test either identical or different tokens. This guarantees that every test case covers a unique combination of the partitions.

### Step 4 – Combine Partitions to Define Test Requirements (ECC)

| Test | C1 (Factory method type) | C2 (Equality condition) | Description                             |
|------|--------------------------|-------------------------|-----------------------------------------|
| T1   | Logical operator `and()`  | Same value              | Compare two identical operator tokens   |
| T2   | String literal "foo"      | Different value         | Compare string "foo" vs "bar"           |

### Step 5 – Test Values and Expected Output

| Test | Input Configuration               | Scenario Description                                    | Expected Behaviour                                     | Expected Output                         |
|------|-----------------------------------|---------------------------------------------------------|--------------------------------------------------------|-----------------------------------------|
| T1   | `Token.and()` vs `Token.and()`    | Compare two identical `and()` tokens                    | The equality check should return `true` because both tokens are of the same type and value | Valid JSON (two identical `and()` tokens) |
| T2   | `Token.string("foo")` vs `Token.string("bar")` | Compare `Token.string("foo")` and `Token.string("bar")` | The equality check should return `false` because the tokens have the same type but different values | Valid JSON (two different `string` tokens with different values) |

---

# SUITE 4 — ECC

### Step 1 – Identify Testable Function
```java
public static String getClassNameFor(Method method)
```

### Purpose
determines how the class name of a given Method object should be returned — either as a fully qualified name or a simple name — based on the global flag shouldPrintQualified

---


### Step 2 – Identify Parameters, Return Types, and Exceptional Behaviour

| Item                     | Description                                                                                              |
| ------------------------ | -------------------------------------------------------------------------------------------------------- |
| **Parameter**            | `Method method` – the reflected method whose declaring class name should be extracted                    |
| **Return Type**          | `String`                                                                                                 |
| **Normal Behavior**      | Returns either `Class.getName()` or `Class.getSimpleName()` depending on the `shouldPrintQualified` flag |
| **Exceptional Behavior** | None explicitly thrown; relies on Java reflection to retrieve class metadata                             |
| **Testing Goal**         | Ensure both return paths (qualified and simple) behave correctly for various class contexts              |

---

### Step 3 – Model the Input Domain

> Each suite must include **at least one interface-based** and **at least one functionality-based** characteristic.

| ID     | Description                                                                         | Type                    |
| ------ | ----------------------------------------------------------------------------------- | ----------------------- |
| **C1** | `shouldPrintQualified` flag determines output format (simple vs. fully qualified)   | **Interface-based**     |
| **C2** | Declaring class type of the provided method (e.g., user-defined vs. built-in class) | **Functionality-based** |


| Characteristic               | b1                    | b2                  |
| ---------------------------- | --------------------- | ------------------- |
| **C1: shouldPrintQualified** | True (qualified name) | False (simple name) |
| **C2: Declaring class type** | User-defined class    | Java built-in class |

- **Completeness**
   - **C1**: The flag `shouldPrintQualified` is either true or false.
   - **C2**: Method’s declaring class is either user-defined or built-in.
- **Disjointness**
   - **C1**: `shouldPrintQualified` is a boolean → only two possible values: true or false.
   - **C2**: A method must belong to some class and all possible sources into either user-defined or built-in.
---

### Step 4 – Combine Partitions to Define Test Requirements (ECC)

**Each-Choice Coverage:** Each distinct choice for each characteristic appears in at least one test case.

| Test   | C1 (qualified) | C2 (declaring class) | Scenario                             | Expected Result           |
| ------ | -------------- | -------------------- | ------------------------------------ | ------------------------- |
| **T1** | True           | User-defined class   | Qualified name of user-defined class | `"com.example.DemoClass"` |
| **T2** | False          | Built-in class       | Simple name of built-in class        | `"String"`                |

---

### Step 5 – Derive Test Values and Expected Values

| Test   | Input Example                                                                    | Expected Output           | Explanation                                            |
| ------ | -------------------------------------------------------------------------------- | ------------------------- | ------------------------------------------------------ |
| **T1** | `DemoClass.getMethod("demoMethod")`; `shouldPrintQualified = true`               | `"com.example.DemoClass"` | Ensures fully qualified name returned when flag = true |
| **T2** | `String.class.getMethod("substring", int.class)`; `shouldPrintQualified = false` | `"String"`                | Tests simple name for built-in class                   |

---


# SUITE 5 - Error Path Pairwise

**Technique:** Pairwise (Focus on *Negative Testing* and **Error Paths**)

**Test Suite:** `Parser_Pairwise_MissingAndFlagsTest`

## Step 1 – Identify Testable Function

```java
`ASTNode Parser.parse(Deque<Token> tokens)`
```

### Purpose
Validate that the parser:
- **Errors early with helpful messages** for malformed inputs (missing RHS after operators, bad sequencing, stray/unclosed parentheses, incomplete function syntax).
- **Handles empty input** by returning `null`.
- **Propagates the “method-expression” flag** to the right subtree when the special `FUNCTION_MATCHER (##)` is present before a function call.

---

### Step 2 – Identify Parameters, Return Types, and Exceptional Behavior

| Item | Description |
|------|-------------|
| **Parameters** | `Deque<Token> tokens` — token stream for a single expression. |
| **Return Type** | `ASTNode` (nullable: `null` for empty input). |
| **Normal Behavior** | Builds an AST from a valid infix expression (strings, `&&`, `||`, parentheses, functions, `##` function matcher). |
| **Exceptional Behavior** | Throws `CompilationException` with **specific**, user-facing messages on structural or sequencing errors. |
| **Testing Goal** | Cover every **pairwise interaction** among: expression positions that **require a RHS**, **next-token availability/validity**, and **parenthesis state**, plus verify **flag propagation** in a successful parse. |

---

### Step 3 – Model the Input Domain

#### 1) Develop Characteristics

| ID | Description | Type |
|----|-------------|------|
| **C1** | **RHS requirement** at the current position (e.g., after `&&`, `||`, `##`, and function start). | Functionality-based |
| **C2** | **Next token availability/validity** (present & valid vs. missing/invalid sequencing like consecutive strings). | Interface-based |
| **C3** | **Parenthesis state** (balanced/NA vs. unclosed/stray-close). | Functionality-based |

#### 2) Partition Characteristics

| Characteristic | b1 | b2 |
|---|---|---|
| **C1: RHS required?** | **Yes** (after `&&`, `||`, `##`, `function(...)` start) | **No** (standalone tokens, closures) |
| **C2: Next token** | **Present & valid** | **Missing / Invalid** (EOF, missing `(`, consecutive strings, etc.) |
| **C3: Parenthesis state** | **Balanced / N/A** | **Unclosed / Stray** |

#### 3) Identify (Possible) Values

| Characteristic | Example (True) | Example (False) |
|---|---|---|
| **C1** | After `string("foo"), and()` → needs RHS | Standalone `closeParen()` (doesn’t “need RHS”) |
| **C2** | `function("endsWith"), openParen(), string("x"), closeParen()` | `function("startsWith"), string("bar")` (missing `(`) / `string("foo"), string("bar")` (consecutive) / EOF |
| **C3** | `openParen(), string("x"), closeParen()` | `openParen(), string("x")` (unclosed) / `closeParen()` (stray) |

**Summary:** 3 binary characteristics ⇒ **2³ = 8** logical combos (we’ll also include one **positive** case for flag propagation).

---

### Step 4 – Combine Partitions to Define Test Requirements

| Test | C1 (RHS?) | C2 (Next token) | C3 (Parens) | Scenario (Test Method) | Expected Result |
|---|---|---|---|---|---|
| **T1** | Yes | **Missing/Invalid** | Balanced | After `##`, no expression.<br>`functionMatcher_noNextToken_afterHash_throwsHelpfulMessage` | `CompilationException` with message starting **"No expression after function matcher start"**. |
| **T2** | Yes | **Missing/Invalid** | Balanced | Function started, but **no `(`** next.<br>`functionCall_withoutOpenParen_throws` | `CompilationException` with message starting **"Open parenthesis required after function start"**. |
| **T3** | Yes | **Missing/Invalid** | Balanced | Function name followed by EOF.<br>`functionCall_noNextToken_afterFunctionStart_throws` | `CompilationException` with message starting **"No expression after function start"**. |
| **T4** | Yes | **Missing/Invalid** | Balanced | `foo &&` (missing RHS).<br>`and_missingRightHandSide_throws` | `CompilationException` with message starting **"No expression after operand &&"**. |
| **T5** | Yes | **Missing/Invalid** | Balanced | `foo ||` (missing RHS).<br>`or_missingRightHandSide_throws` | `CompilationException` with message starting **"No expression after operand ||"**. |
| **T6** | No | **Missing/Invalid** | **Stray** | Single `)` token.<br>`strayCloseParen_throwsUnexpectedCloseParenthesis` | `CompilationException` with **"Unexpected close parenthesis."** |
| **T7** | Yes | **Present & valid** | **Unclosed** | `( x` (missing `)`).<br>`unclosedParenthesis_throws` | `CompilationException` with **"Unclosed parenthesis"**. |
| **T8** | No | **Missing/Invalid** | Balanced | `foo bar` (two consecutive strings).<br>`consecutiveStrings_throwsCantHaveTwoConsecutiveStrings` | `CompilationException` with **"Can't have two consecutive strings or strings without logic"**. |
| **T9** | — | **Missing** | Balanced | Empty input.<br>`emptyDeque_returnsNull` | Returns **`null`** (no exception). |
| **T10** | Yes (after `##`) | **Present & valid** | Balanced | `foo ## endsWith("bar")`.<br>`functionMatcher_flagsRightSubtreeAsMethodExpression` | **Parses OK**; right subtree is flagged: `containsMethodExpression() == true`. |

> **Pair-wise note:** T1–T8 collectively cover all pairwise combinations across **C1–C3** (Yes/No × Present/Invalid × Balanced/Unbalanced), while **T10** is a **positive** control ensuring the special **flag propagation** behavior works on a valid input. **T9** documents the null contract for empty streams.

---

### Step 5 – Derive Test Values

| Test | Token Sequence (using helpers) | Expected |
|---|---|---|
| **T1** | `dq(functionMatcher())` // `## <EOF>` | Throw; message starts **"No expression after function matcher start"**. |
| **T2** | `dq(function("startsWith"), string("bar"))` // `startsWith bar` | Throw; message starts **"Open parenthesis required after function start"**. |
| **T3** | `dq(function("endsWith"))` // `endsWith <EOF>` | Throw; message starts **"No expression after function start"**. |
| **T4** | `dq(string("foo"), and())` // `foo &&` | Throw; message starts **"No expression after operand &&"**. |
| **T5** | `dq(string("foo"), or())` // `foo ||` | Throw; message starts **"No expression after operand ||"**. |
| **T6** | `dq(closeParen())` // `)` | Throw; **"Unexpected close parenthesis."** |
| **T7** | `dq(openParen(), string("x"))` // `( x` | Throw; **"Unclosed parenthesis"**. |
| **T8** | `dq(string("foo"), string("bar"))` // `foo bar` | Throw; **"Can't have two consecutive strings or strings without logic"**. |
| **T9** | `dq()` // empty | **`null`** (no exception). |
| **T10** | `dq(string("foo"), functionMatcher(), function("endsWith"), openParen(), string("bar"), closeParen())` // `foo ## endsWith("bar")` | **Parses OK**; assert `ast.children[1].containsMethodExpression()` is **true**. |

---

# SUITE 6 — PWC
**Technique:** Pair-Wise Coverage

## Test Suite: MethodInstrumentationAgentPWCTest

### Step 1 – Identify Testable Function

```java
public static byte[] readAllBytes(InputStream inputStream) throws IOException
```

### Purpose
This method reads all bytes from an InputStream into a byte[] array. Its complexity stems from the strict exception handling required in Java I/O, specifically:
- **Exception handling**: Capturing a primary IOException during read/write operations.**
- **Resource cleanup**: Ensuring inputStream.close() is called in the finally block.
- **Suppressed Exceptions**: If the primary operation fails, ensuring any subsequent close() failure is correctly suppressed within the primary exception.-
- **Close Exception Propagation**: If the primary operation succeeds, ensuring a subsequent close() failure is thrown directly.
---


### Step 2 – Identify Parameters, Return Types, and Exceptional Behavior

| Item | Description |
|------|--------------|
| **Parameters** | • `inputStream`: The stream from which data is read. |
| **Return Type** | `byte[]` |
| **Normal Behavior** | Returns a `byte[]` containing all stream data. |
| **Exceptional Behavior** | Throws an `IOException` if reading fails, or if `close()` fails after a successful read. |
| **Testing Goal** | Verify that every possible interaction pair between the **Read Status (F1)**, **Data Size (F2)**, and **Close Status (F3)** behaves correctly, especially regarding exception suppression and propagation. |

---

### Step 3 – Model the Input Domain

#### 1. Develop Characteristics

| ID | Description | Type |
|----|--------------|------|
| **C1** | Primary operation failure (Read/Write throws `IOException`) | Functionality-based |
| **C2** | Data volume required for reading (loop iteration) | Interface-based |
| **C3** | Stream closure failure (`inputStream.close()` throws `IOException`) | Functionality-based |

---

#### 2. Partition Characteristics

| Characteristic | b1 | b2 |
|----------------|----|----|
| C1 = R/W status | R-OK | R-Fail |
| C2 = Data Volume | <= 4096 bytes | > 4096 bytes |
| C3 = Close Status | C-OK | C-Fail |
  
---

#### 3. Identify (Possible) Values

| Characteristic | Example (True) | Example (False) |
|----------------|----------------|----------------|
| **C1: R/W status** | All bytes would be read | readException will be thrown |
| **C2: Data Volume** | byte[] expectedData = new byte[]{0x01, 0x02, 0x03, 0x04} | Arrays.fill(expectedData /*with size 5000*/, (byte) 0xFF) |
| **C3: Close Status** | Method would be closed | closeException will be thrown |

---

- **Completeness**
   - **C1**: Any read/write operation either succeeds or fails (throws an exception).
   - **C2**: Data volume can only be within the two defined ranges (≤ 4096 or > 4096).
   - **C3**: A stream close operation either succeeds or throws an exception.
- **Disjointness**
   - **C1 and C3**: The method can’t both succeed and fail.
   - **C2**: Every possible data size belongs to exactly one of these two partitions.

---

### Step 4 – Combine Partitions to Define Test Requirements

| Test | F1 (Read) | F2 (Size) | F3 (Close) | Scenario | Covered Pairs (Example) | Expected Result |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| **T1** | R-OK | <= 4096 | C-OK | Standard small data read, all success. | (R-OK, <= 4096), (<= 4096, C-OK), (R-OK, C-OK) | Returns data successfully. |
| **T2** | R-OK | <= 4096 | C-FAIL | Successful read, but resource cleanup fails. | (R-OK, C-FAIL), (<= 4096, C-FAIL) | **Throws** the close() exception directly. |
| **T3** | R-OK | > 4096 | C-OK | Multi-buffer read, all success. | (R-OK, > 4096), (> 4096, C-OK) | Returns data successfully. |
| **T4** | R-FAIL | <= 4096 | C-OK | Read/Write fails, close succeeds. | (R-FAIL, C-OK), (R-FAIL, <= 4096) | Throws **primary** read exception; no suppressed exception. |
| **T5** | R-FAIL | > 4096 | C-FAIL | Read/Write fails, close also fails. | (R-FAIL, C-FAIL), (> 4096, C-FAIL) | Throws **primary** read exception; close() exception is **suppressed**. |
---

### Step 5 – Derive Test Values

| Test | F1 Input (Read behavior) | F2 Input (Data Size) | F3 Input (Close behavior) | Expected Output | Explanation |
| :---- | :---- | :---- | :---- | :---- | :---- |
| **T1** | Returns 4 bytes | **\< 4096 bytes** | **Close succeeds** (null exception) | Returns the 4 bytes. | Verifies the happy path for the most common input size. |
| **T2** | Returns 2 bytes | **\< 4096 bytes** | **Close fails** (throws IOException) | Throws the IOException from close(). | Verifies finally block logic where exception \== null, causing close() failure to be thrown. |
| **T3** | Returns **5000 bytes** | **\> 4096 bytes** | **Close succeeds** (null exception) | Returns the 5000 bytes. | Verifies correct loop iteration and stream finalization. |
| **T4** | **Throws exception** immediately | N/A (Read fails before size matters) | **Close succeeds** (null exception) | Throws the **primary** exception; getSuppressed().length \== 0\. | Verifies finally block logic where exception \!= null and close() succeeds. |
| **T5** | **Throws exception** immediately | N/A | **Close fails** (throws IOException) | Throws the **primary** exception; close() exception is correctly added as a suppressed exception. | Verifies the critical exception suppression logic. |
---

# SUITE 7 — BCC
**Technique:** Boundary & Condition Coverage (BCC)

## Test Suite: FunctionCallRecorderWithValueCapturing_BCC_Test

### Step 1 – Identify Testable Function

```java
getValueAsString()
```

### Purpose
Converts any input object into a readable string.  
Handles:
- Nulls
- Primitive/Object/Empty arrays
- Exception during `toString()`

---

### Step 2 – Identify Parameters, Return Types, and Exceptional Behavior

| Item | Description |
|------|--------------|
| **Parameter** | `Object value` |
| **Return Type** | `String` |
| **Exceptional Behavior** | Returns `"ARG_TOSTRING_EXCEPTION"` if `toString()` fails |

---

### Step 3 – Model the Input Domain

| ID | Description | Type |
|----|--------------|------|
| **C1** | value == null | Interface-based |
| **C2** | value is array | Interface-based |
| **C3** | array subtype | Functionality-based |
| **C4** | exception occurs | Functionality-based |

| ID | Description | Type | b1 | b2 | b3 | b4 |
|----|--------------|------|----|----|----|----|
| **C1** | `value == null` | Interface-based | `null` | `≠ null` | – | – |
| **C2** | `isArray(value)` | Interface-based | `array` | `not array` | – | – |
| **C3** | `array subtype` | Functionality-based | `primitive array` | `object array` | `empty array` | `non array` |
| **C4** | `exception occurs` | Functionality-based | `exception` | `no exception` | – | – |

---
Base: ≠ null | Not array | Non-array | No error
Total test cases = 1 + ((2−1) + (2−1) + (4−1) + (2−1)) = 7
### Step 4 – Combine Partitions to Define Test Requirements

| Test | C1 (Value == null?) | C2 (Is Array?) | C3 (Array Subtype) | C4 (Exception?) | Scenario |
|------|----------------------|----------------|--------------------|----------------|-----------|
| **T1** | ≠ null | Not array | Non-array | No error | Normal object input |
| **T2** | ≠ null | Array | Non-array | Has error | toString() throws exception |
| **T3** | ≠ null | Not array | Empty array | No error | Empty array input |
| **T4** | ≠ null | Array | Object array | No error | Object array input |
| **T5** | ≠ null | Not array | Primitive array | No error | Primitive array input |
| **T6** | ≠ null | Array | Non-array | No error | Mixed array type input |
| **T7** | null | Array | Non-array | No error | Null input value |

---

### Step 5 – Derive Test Values

| Test | Input Value | Expected Output |
|------|--------------|-----------------|
| **T1** | `"Hello"` | `"Hello"` |
| **T2** | `new Object(){ public String toString(){ throw new RuntimeException(); }}` | `"ARG_TOSTRING_EXCEPTION"` |
| **T3** | `int[] {}` | `"[]"` |
| **T4** | `String[] {"A", "B"}` | `"[A, B]"` |
| **T5** | `int[] {1, 2, 3}` | `"[1, 2, 3]"` |
| **T6** | `new Object[] {1, "A", null}` | `"[1, A, null]"` |
| **T7** | `null` | `"null"` |

---
# SUITE 8 — BCC  
**Technique:** Base Choice Coverage (BCC)

## Test Suite: AgentMetadataBCCTest  
**File:** `AgentMetadataBCCTest.java`

---

### Step 1 – Identify Testable Function
- **Class:** `com.github.beothorn.agent.MethodInstrumentationAgent`  
- **Method Tested:** `getExecutionMetadataAsJson()`  
- **Purpose:**  
  Generates JSON metadata describing the Java agent execution environment. Handles:  
  - Initialized vs. uninitialized agent state  
  - Complete vs. partial metadata  
  - Serialization success/failure  

---

### Step 2 – Identify Parameters, Return Types, and Exceptional Behaviour

| Item | Description |
|------|------------|
| Parameter | None (all internal) |
| Return Type | String (JSON metadata) |
| Exceptional Behaviour | Returns valid JSON even if some data is missing or the agent is uninitialized |

---

### Step 3 – Model the Input Domain

| ID | Characteristic | Type | b1 | b2 | Notes |
|----|---------------|------|----|----|------|
| C1 | Agent initialization state | Interface-based | Initialized | Uninitialized | Determines agent readiness |
| C2 | Metadata completeness | Functionality-based | Complete | Partial | Some fields may be missing |
| C3 | Serialization process | Functionality-based | Success | Failure | JSON serialization result |

**Complete and Disjoint:** Each test case covers a unique combination of characteristics. All characteristic values (b1/b2) are tested independently.  

### Base Choice Coverage Formula:
Total test cases = 1 + ∑_(i=1)^n (b_i - 1)  
Total test cases = 1 + ((2−1) + (2−1) + (2−1)) = 4

---

### Step 4 – Combine Partitions to Define Test Requirements (BCC)

| Test | C1 (Agent state) | C2 (Metadata) | C3 (Serialization) | Description |
|------|-----------------|---------------|-------------------|-------------|
| T1 (Base) | Initialized (b1) | Complete (b1) | Success (b1) | Normal initialized state, complete metadata, serialization succeeds |
| T2 | Initialized (b1) | Partial (b2) | Success (b1) | Metadata partially available, some keys may be missing |
| T3 | Uninitialized (b2) | Complete (b1) | Success (b1) | Agent uninitialized, returns JSON with empty fields |
| T4 | Initialized (b1) | Complete (b1) | Failure (b2) | Simulation of serialization failure, method returns valid JSON |

**Base Case Selection:** T1 (C1=b1, C2=b1, C3=b1) is chosen as the base case because it represents the normal scenario. It serves as a reference for testing variations in other characteristics.  

---

### Step 5 – Test Values and Expected Output

| Test | Input Configuration | Scenario Description | Expected Behaviour | Expected Output |
|------|-------------------|-------------------|------------------|----------------|
| T1 | Normal values for all fields, start/stop present | Agent initialized; metadata complete; serialization succeeds | Generates valid JSON | Valid JSON string |
| T2 | Missing some fields (e.g., output empty), start/stop present | Partial metadata | JSON with partial fields | Valid JSON (some keys may be missing) |
| T3 | Empty strings for all fields, no start/stop | Agent uninitialized | Returns empty JSON fields | Valid JSON (may be empty fields) |
| T4 | Values that simulate serialization failure | Serialization failure simulation | Handles failure gracefully | Valid JSON (serialization succeeds) |

---

# SUITE 9 — MBCC

**Technique:** Multiple Base Choice Coverage (MBCC)

---

### Step 1 - Identify Testable Function
- **Class:** `com.github.beothorn.agent.parser.ElementMatcherFromExpression`
- **Method Tested:** `forExpression(String input)`
- **Purpose:** Parses an input string into an `ElementMatcher` that identifies whether a class name matches based on substring presence and logical OR (\|\|) operations.

---

### Step 2 - Identify Parameters, Return Types, Return Values, and Exceptional Behaviour

| Item | Description |
|------|--------------|
| **Parameter** | `String input`: textual expression representing class name patterns (e.g.,`"Order\|\|Payment"`). |
| **Return Type** | `ElementMatcherFromExpression`: object that can generate a `getClassMatcher()` to test class name matches. |
| **Return Value** | A valid `ElementMatcherFromExpression` instance when the expression is syntactically correct. |
| **Exceptional Behaviour** | - `CompilationException` when the input contains whitespace or malformed operators.<br> - `NullPointerException` when the input string is empty (`""`). |

---

### Step 3 - Model the Input Domain

| ID | Characteristic | Type | b1 | b2 | b3 |
|----|----------------|------|----|----|----|
| **C1** | **Number of identifiers** | *Interface-based* | 0 identifiers | 1 identifier | >1 identifiers |
| **C2** | **Name match** | *Functionality-based* | True | False |  |
| **C3** | **Whitespace presence** | *Interface-based* | False (valid) | True (invalid) |  |
| **C4** | **Operator structure** | *Interface-based* | Valid | Invalid |  |

---

### Step 4 - Combine Partitions to Define Test Requirements (MBCC)

- **Base case:** `"Order"` (C1=b2, C2=b1, C3=b1, C4=b1).  
- **Total = 6 Test Requirements (TR1–TR6).**

| TR | Varied Characteristic | Combination (C1–C4) | Description |
|----|------------------------|----------------------|--------------|
| **TR1 (Base)** | — | b2, b1, b1, b1 | Valid single identifier that matches a class name. |
| **TR2** | C1=b1 | b1, b1, b1, b1 | Empty input (no identifiers). |
| **TR3** | C1=b3 | b3, b1, b1, b1 | Two identifiers joined with valid \|\|. |
| **TR4** | C2=b2 | b2, b2, b1, b1 | Case mismatch (no name match). |
| **TR5** | C3=b2 | b2, b1, b2, b1 | Whitespace present; invalid. |
| **TR6** | C4=b2 | b2, b1, b1, b2 | Malformed operator; invalid. |

---

### Step 5 - Derive Test Values and Expected Values

| ID | Input Expression | Class Under Test | Expected Behaviour | Expected Result |
|----|-------------------|------------------|--------------------|-----------------|
| **TR1** | `"Order"` | `OrderService` | Single valid identifier | True (match) |
|  |  | `PaymentClient`, `FooBar` |  | False (no match) |
| **TR2** | `""` | any class | Empty input (0 identifiers) | `NullPointerException` |
| **TR3** | `"Order\|\|Payment"` | `OrderService`, `PaymentClient` | Multiple identifiers joined with \|\|; | True (match) |
|  |  | `FooBar` |  | False (no match) |
| **TR4** | `"orderservice"` | `OrderService` | Case mismatch | False (no match) |
| **TR5** | `"Order \|\| Payment"` | any class | Contains whitespace | `CompilationException` |
| **TR6** | `"Order\|\|”` | any class | Malformed operator at end | `CompilationException` |

---

# SUITE 10 — MBCC
**Technique:** Multiple Base Choice Coverage (MBCC)

---

### Step 1 – Identify Testable Function
- **Class:** `com.github.beothorn.agent.parser.ClassAndMethodMatcher`  
- **Method Tested:** `matcher(ElementMatcher<NamedElement> classMatcher, ElementMatcher<MethodDescription> methodMatcher)`  
- **Purpose:** Construct a `ClassAndMethodMatcher` that preserves the behavior of both provided matchers. The returned **`classMatcher`** and **`methodMatcher`** must behave identically to the inputs.

---

### Step 2 – Identify Parameters, Return Types, Return Values, and Exceptional Behaviour

| Item | Description |
|---|---|
| **Parameter** | `classMatcher`: `ElementMatcher<NamedElement>` (e.g., `named("a.b.Foo")`, `any()`)  <br>`methodMatcher`: `ElementMatcher<MethodDescription>` (e.g., `named("run")`, `nameStartsWith("ru")`, `not(named("run"))`, `namedIgnoreCase("RUN")`) |
| **Return Type** | `ClassAndMethodMatcher` |
| **Return Value** | An object exposing **`classMatcher`** and **`methodMatcher`** whose matching semantics equal the two inputs |
| **Exceptional Behaviour** | None expected for valid inputs (factory stores and exposes the two matchers) |

---

### Step 3 – Model the Input Domain

> Each suite must include **at least one interface-based** and **at least one functionality-based** characteristic.  
> Fixtures used in tests: `Foo { void run(); void stop(); }`, `Bar { void run(); }`.

| ID | Characteristic | Type | b1 | b2 | Notes |
|---|---|---|---|---|---|
| **C1** | Class matcher form | **Interface-based** | `named(Foo)` | `any()` | Specific vs wildcard |
| **C2** | *Method name match for candidate `run`* | **Functionality-based** | **True** | **False** | Whether `run` should match; realized by choosing matcher configs (exact/prefix/negated/ignore-case) |
| **C3** | Negation wrapper (method matcher) | **Interface-based** | none | `not(...)` | Logical NOT around method matcher |
| **C4** | Case sensitivity option | **Interface-based** | sensitive | ignore-case | `named` vs `namedIgnoreCase` (and analogous for startsWith) |

---

### Step 4 – Combine Partitions to Define Test Requirements (MBCC)

Base case: `C1=b1`, `C2=b1`, `C3=b1`, `C4=b1`  
→ `cm = named(Foo)`, `mm = named("run")`, no negation, case-sensitive.

| TR | Varied Characteristic | Combination (C1–C4) | Description |
|---|---|---|---|
| **TR1 (Base)** | —  | b1, b1, b1, b1 | Exact class + exact method |
| **TR2**        | C1 | b2, b1, b1, b1 | Class matcher becomes `any()` |
| **TR3**        | C2 | b1, b2, b1, b1 | Method matcher uses `nameStartsWith("ru")` |
| **TR4**        | C3 | b1, b1, b2, b1 | Apply `not(named("run"))` (negation wrapper) |
| **TR5**        | C4 | b1, b1, b1, b2 | Case-insensitive via `namedIgnoreCase` |

---

### Step 5 – Derive Test Values and Expected Values

| ID | Input Configuration (passed to `matcher(...)`) | Class/Method Probes | Expected Behaviour | Expected Result |
|---|---|---|---|---|
| **TR1** | `cm = named(Foo)`, `mm = named("run")` | `Foo.run`, `Foo.stop`, `Bar.run` | Only `(Foo, run)` matches | `Foo.run = true`; `Foo.stop = false`; `Bar.run = false` |
| **TR2** | `cm = any()`, `mm = named("run")` | `Foo.run`, `Bar.run` | All classes match; method rule unchanged | `Foo.run = true`; `Bar.run = true` |
| **TR3** | `cm = named(Foo)`, `mm = nameStartsWith("ru")` | `Foo.run`, `Foo.stop` | Prefix “ru” matches `run`, not `stop` | `Foo.run = true`; `Foo.stop = false` |
| **TR4** | `cm = named(Foo)`, `mm = not(named("run"))` | `Foo.run`, `Foo.stop` | Negation flips results for `run` | `Foo.run = false`; `Foo.stop = true` |
| **TR5** | `cm = namedIgnoreCase("FOO")`, `mm = namedIgnoreCase("RUN")` | `Foo.run`, `Bar.run` | Ignore case for both class & method | `Foo.run = true`; `Bar.run = false` |

---

## How to Run Tests

Use Java version 8.0

Run all test suites via Gradle:

```bash
./gradlew test
```

Run a specific suite:

```bash
./gradlew test --tests "com.yourpkg.SuiteName_Test"
```



