//------ Form Validation ------ //
function validateNumericInput(input) {
    // Remove any non-numeric characters
    input.value = input.value.replace(/\D/g, '');
}

// Function to handle input focus behavior for number inputs
function handleNumberInputFocus(inputElement) {
    // Remove any non-numeric or non-decimal characters except for the last dot
    inputElement.value = inputElement.value
        .replace(/[^\d.]/g, '')
        .replace(/^\./, '') // Remove leading dot
        .replace(/\.(?=.*\.)/g, ''); // Remove any subsequent dots

    // Limit to two decimal places
    inputElement.value = inputElement.value.replace(/(\.\d\d)\d+/g, '$1');

    // Check if the input value is valid (numeric or empty)
    if (/^\d*\.?\d{0,2}$/.test(inputElement.value)) {
        // If valid, update the input value to maintain focus
        inputElement.defaultValue = inputElement.value;
    } else {
        // If invalid, revert to the default value to prevent focus change
        inputElement.value = inputElement.defaultValue || '';
    }
}

function setupDateValidation(fromId, untilId) {
    var fromElement = document.getElementById(fromId);
    var untilElement = document.getElementById(untilId);

    if (fromElement && untilElement) {
        fromElement.addEventListener('change', function () {
            validateDates(fromId, untilId);
        });
        untilElement.addEventListener('change', function () {
            validateDates(fromId, untilId);
        });
    }
}

function validateDates(fromId, untilId) {
    var fromDate = document.getElementById(fromId).value;
    var untilDate = document.getElementById(untilId).value;

    if (fromDate && untilDate) {
        if (fromDate > untilDate) {
            alert("The 'From' date must be earlier than the 'Until' date.");
            document.getElementById(untilId).value = ""; // Clear the until date if invalid
        }
    }
}

function parseDate(input) {
    // Parse date in 'yyyy-mm-dd' format
    const dateParts = input.split('-');
    if (dateParts.length === 3) {
        const year = parseInt(dateParts[0], 10);
        const month = parseInt(dateParts[1], 10) - 1; // Months are zero-indexed in JavaScript Date
        const day = parseInt(dateParts[2], 10);
        return new Date(year, month, day);
    }
    return null;
}

//------ Visuals ------ //
document.getElementById('check').addEventListener('change', function () {
    let sidebar = document.getElementById('sidebar');
    let content = document.getElementById('content');
    if (this.checked) {
        sidebar.style.visibility = 'visible';
        sidebar.style.left = '0';

        content.style.marginLeft = '150px';
    } else {
        sidebar.style.left = '-250px';
        sidebar.style.visibility = 'hidden';

        content.style.marginLeft = '0';
    }
});

//---Sidebar button coloring
const links = document.querySelectorAll('.sidebar a');
links.forEach(link => {
    link.addEventListener('click', function (event) {
        // Remove active class from all links
        links.forEach(l => l.classList.remove('active'));

        // Add active class to clicked link
        this.classList.add('active');
    });
});


//--- Change panels
function changePanel(selectedPanel, numOfDivs) {
    let divPanels = document.querySelectorAll(`div.content > div:nth-child(n + 1):nth-child(-n + ${numOfDivs})`);
    divPanels.forEach(div => { div.style.display = 'none'; })

    const selectedElement = document.getElementById(selectedPanel);

    // Check if the element was found
    if (selectedElement) {
        console.log('Element was found ', selectedElement);
        selectedElement.style.display = 'block';  // Make the selected panel visible
    } else {
        console.error(`Element with ID "${selectedPanel}" not found.`);  // Handle missing element
    }
}

function backToTable(show, hide) {
    document.getElementById(show).style.display = 'block';
    document.getElementById(hide).style.display = 'none';
}

//-----Table functionality/-----//
function searchTable(tableId, inputId) {
    // Get the search input value
    const searchInput = document.getElementById(inputId).value.toLowerCase();

    // Get the table rows of the specific table
    const table = document.getElementById(tableId);
    const rows = table.querySelectorAll('tbody tr');

    // Loop through the rows and hide those that don't match the search
    rows.forEach(row => {
        const cells = row.querySelectorAll('td');
        let rowContainsSearchTerm = false;

        cells.forEach(cell => {
            if (cell.textContent.toLowerCase().includes(searchInput)) {
                rowContainsSearchTerm = true;
            }
        });

        if (rowContainsSearchTerm) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

function sortTable(columnIndex, tableId) {
    const table = document.getElementById(tableId);
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const isAscending = table.getAttribute('data-sort-order') === 'asc';
    const direction = isAscending ? 1 : -1;

    rows.sort((rowA, rowB) => {
        const cellA = rowA.children[columnIndex].textContent.trim();
        const cellB = rowB.children[columnIndex].textContent.trim();

        // Try to parse as dates in 'yyyy-mm-dd' format
        const dateA = parseDate(cellA);
        const dateB = parseDate(cellB);

        if (dateA && dateB) {
            return (dateA - dateB) * direction;
        } else {
            // If not dates, proceed with numeric or string comparison
            const numA = parseFloat(cellA);
            const numB = parseFloat(cellB);

            if (!isNaN(numA) && !isNaN(numB)) {
                return (numA - numB) * direction;
            } else {
                const strA = cellA.toLowerCase();
                const strB = cellB.toLowerCase();

                if (strA < strB) {
                    return -1 * direction;
                }
                if (strA > strB) {
                    return 1 * direction;
                }
                return 0;
            }
        }
    });

    // Reorder rows in the table based on the sorted rows array
    rows.forEach(row => tbody.appendChild(row));

    // Toggle sort order attribute
    table.setAttribute('data-sort-order', isAscending ? 'desc' : 'asc');
}

//---Seller
function populateClientForm(row) {
    document.getElementById("searchAndTable").style.display = 'none';
    document.getElementById("editClientDiv").style.display = 'block';

    const cells = row.querySelectorAll('td');
    const form = document.getElementById('editClientForm');
    form.elements['AFM'].value = cells[0].textContent.trim();
    form.elements['name'].value = cells[1].textContent.trim();
    form.elements['surname'].value = cells[2].textContent.trim();
    form.elements['username'].value = cells[3].textContent.trim();
    form.elements['phonenumber'].value = cells[4].textContent.trim();
    form.elements['progID'].value = cells[5].textContent.trim();
}

function populateBillForm(row) {
    // Display the billClientForm and hide the billClient-Search-Table
    document.getElementById("billClient-Search-Table").style.display = 'none';
    document.getElementById("billClientForm").style.display = 'block';

    // Get the cells of the clicked row
    const cells = row.getElementsByTagName('td');

    // Populate AFM and username in the form
    document.getElementById('AFM').value = cells[0].textContent.trim(); // Adjust index based on your table structure
    document.getElementById('username').value = cells[3].textContent.trim(); // Adjust index based on your table structure
}

//---Admin
function populateSellerForm(row){
    document.getElementById("editSeller-Search-Table").style.display = 'none';
    document.getElementById("editSellerDiv").style.display = 'block';

    const cells = row.querySelectorAll('td');
    const form = document.getElementById('editSellerForm');

    form.elements['SELLERID'].value = cells[0].textContent.trim();
    form.elements['name'].value = cells[1].textContent.trim();
    form.elements['surname'].value = cells[2].textContent.trim();
    form.elements['username'].value = cells[3].textContent.trim();

    document.body.scrollTop = document.documentElement.scrollTop = 0;

}

function populateProgramForm(row){
    document.getElementById("editProgram-Search-Table").style.display = 'none';
    document.getElementById("editProgramDiv").style.display = 'block';

    const cells = row.querySelectorAll('td');
    const form = document.getElementById('editProgramForm');

    form.elements['programID'].value = cells[0].textContent.trim();
    form.elements['talkingTime'].value = cells[1].textContent.trim();
    form.elements['fixedCharge'].value = cells[2].textContent.trim();
    form.elements['extraCharge'].value = cells[3].textContent.trim();
    form.elements['sellerID'].value = cells[4].textContent.trim();

    document.body.scrollTop = document.documentElement.scrollTop = 0;

}